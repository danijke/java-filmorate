package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface UserFilmLikesStorage {

    List<Long> findLikedFilmsByUserId(Long userId);

    List<Long> findSimilarUsers(List<Long> filmIds, Long excludeUserId);

    List<Film> findRecommendedFilms(List<Long> userIds, Long excludeUserId);
}
