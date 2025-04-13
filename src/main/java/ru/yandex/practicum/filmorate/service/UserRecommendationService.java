package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRecommendationService {
    private final FilmStorage filmStorage;
    private final FilmMapper filmMapper;
    private final NamedParameterJdbcTemplate namedParameterJdbc;
    private final JdbcTemplate jdbc;
    private static final int topN = 10;

    public Collection<Film> getRecommendations(Long userId) {
        String currentUserLikesQuery = """
                SELECT film_id
                FROM user_film_likes
                WHERE user_id = ?
                """;
        List<Long> currentUserLikedFilms = jdbc.queryForList(currentUserLikesQuery, Long.class, userId);

        if (currentUserLikedFilms.isEmpty()) {
            log.info("Пользователь ID_{} не лайкал фильмы", userId);
            return Collections.emptyList();
        }
        //Находим пользователей, которые лайкали те же фильмы
        String similarUsersQuery = """
                SELECT *
                FROM user_film_likes
                WHERE film_id IN (:filmsIds)
                AND user_id != :userId
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmsIds", currentUserLikedFilms)
                .addValue("userId", userId);

        List<Map<String, Object>> allPairs = namedParameterJdbc
                .queryForList(similarUsersQuery, params);

        if (allPairs.isEmpty()) {
            log.info("Для пользователя ID_{} не найдено пользователей с похожими вкусами", userId);
            return Collections.emptyList();
        }
        //Группируем пользователей по кол-ву совпадений, затем сортируем
        Map<Long, Integer> similarUsers = allPairs.stream()
                .collect(Collectors.groupingBy(
                        entry -> ((Long) entry.get("user_id")),
                        Collectors.summingInt(value -> 1)
                ));

        List<Map.Entry<Long, Integer>> sortedSimilarUsers = similarUsers.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .toList();

        //Выбираем топ-N наиболее похожих пользователей
        List<Long> topSimilarUsers = sortedSimilarUsers.stream()
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();

        //Получаем фильмы, которые понравились похожим пользователям
        // и которых нет в списке оцененных текущим пользователем
        String recommendedFilmsQuery = """
                SELECT DISTINCT f.*
                FROM films f
                JOIN user_film_likes ufl on f.film_id = ufl.film_id
                WHERE ufl.user_id IN (:userIds)
                AND ufl.film_id NOT IN (
                SELECT film_id FROM user_film_likes WHERE user_id = :currentUserId)
                """;

        params = new MapSqlParameterSource()
                .addValue("userIds", topSimilarUsers)
                .addValue("currentUserId", userId);

        List<Film> recommendedFilms = namedParameterJdbc.query(recommendedFilmsQuery, params, filmMapper).stream()
                .map(filmStorage::wrapSetFilmEntity)
                .toList();

        if (recommendedFilms.isEmpty()) {
            log.info("Для пользователя ID_{} не найдено рекомендаций", userId);
            return Collections.emptyList();
        }
        return recommendedFilms;
    }
}
