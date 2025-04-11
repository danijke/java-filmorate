package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface DirectorStorage {
    Optional<Director> saveDirector(Director director);

    void removeDirector(Long id);

    Optional<Director> findDirectorById(Long id);

    Collection<Director> getDirectors();

    Optional<Director> updateDirector(Director newDirector);

    List<Long> getExistingDirectorsIds();

    Collection<Director> findDirectorsByFilmId(Film film);

    void saveFilmDirectors(Long filmId, Set<Director> directors);

    void updateFilmDirectors(Long filmId, Set<Director> directors);
}
