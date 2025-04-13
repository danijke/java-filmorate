package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.UserFilmLikesStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRecommendationService {
    private final UserFilmLikesStorage userFilmLikesStorage;

    public Collection<Film> getRecommendations(Long userId) {

        List<Long> currentUserLikedFilms = userFilmLikesStorage.findLikedFilmsByUserId(userId);

        if (currentUserLikedFilms.isEmpty()) {
            log.info("Пользователь ID_{} не лайкал фильмы", userId);
            return Collections.emptyList();
        }

        List<Long> topSimilarUsers = userFilmLikesStorage.findSimilarUsers(currentUserLikedFilms, userId);

        return userFilmLikesStorage.findRecommendedFilms(topSimilarUsers, userId);
    }
}
