package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserFilmLikesStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserFilmLikesDbStorage extends BaseDbStorage<Film> implements UserFilmLikesStorage {
    private static final int topN = 10;
    private final NamedParameterJdbcTemplate namedParameterJdbc;
    private final FilmStorage filmStorage;

    public UserFilmLikesDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper,
                                  NamedParameterJdbcTemplate namedParameterJdbc,
                                  FilmStorage filmStorage) {
        super(jdbc, mapper);
        this.namedParameterJdbc = namedParameterJdbc;
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Long> findLikedFilmsByUserId(Long userId) {
        String currentUserLikesQuery = """
                SELECT film_id
                FROM user_film_likes
                WHERE user_id = ?
                """;
        return jdbc.queryForList(currentUserLikesQuery, Long.class, userId);
    }

    @Override
    public List<Long> findSimilarUsers(List<Long> filmIds, Long excludeUserId) {
        String similarUsersQuery = """
                SELECT *
                FROM user_film_likes
                WHERE film_id IN (:filmsIds)
                AND user_id != :userId
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmsIds", filmIds)
                .addValue("userId", excludeUserId);

        List<Map<String, Object>> allPairs = namedParameterJdbc
                .queryForList(similarUsersQuery, params);

        if (allPairs.isEmpty()) {
            log.info("Для пользователя ID_{} не найдено пользователей с похожими вкусами", excludeUserId);
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
        return sortedSimilarUsers.stream()
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public List<Film> findRecommendedFilms(List<Long> topSimilarUserIds, Long excludeUserId) {
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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userIds", topSimilarUserIds)
                .addValue("currentUserId", excludeUserId);

        List<Film> recommendedFilms = namedParameterJdbc.query(recommendedFilmsQuery, params, mapper).stream()
                .map(filmStorage::wrapSetFilmEntity)
                .toList();

        if (recommendedFilms.isEmpty()) {
            log.info("Для пользователя ID_{} не найдено рекомендаций", excludeUserId);
            return Collections.emptyList();
        }
        return recommendedFilms;
    }
}
