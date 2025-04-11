package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director get(Long directorId) {
        return directorStorage.findDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + directorId + " не найден"));
    }

    public Director create(Director director) {
        log.trace("Добавление режиссераа: {}", director);
        return directorStorage.saveDirector(director)
                .orElseThrow(() -> new NotFoundException("Ошибка при сохранении режиссера"));
    }

    public Director update(Director newDirector) {
        if (newDirector.getId() == null) throw new ValidationException("id должен быть указан");
        return directorStorage.updateDirector(newDirector)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + newDirector.getId() + " не найден"));
    }

    public Collection<Director> findAll() {
        return directorStorage.getDirectors();
    }

    public void delete(Long directorId) {
        directorStorage.removeDirector(directorId);
    }

    public void validateDirectors(Set<Director> directors) {
        if (directors != null) {
            Set<Long> filmDirectorsIds = directors.stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());

            List<Long> existingDirectorsIds = directorStorage.getExistingDirectorsIds();

            Set<Long> existingDirectorsIdsSet = new HashSet<>(existingDirectorsIds);
            Set<Long> invalidDirectorsIds = new HashSet<>(filmDirectorsIds);
            invalidDirectorsIds.removeAll(existingDirectorsIdsSet);

            if (!invalidDirectorsIds.isEmpty()) {
                String errorMessage = String.format("Недействительные id режиссеров: %s", invalidDirectorsIds);
                throw new NoSuchElementException(errorMessage);
            }
        }
    }
}
