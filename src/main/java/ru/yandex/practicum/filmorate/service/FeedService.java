package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;

    public void addEvent(Event event) {
        feedStorage.saveEvent(event);
        log.info("Добавлено событие: {}", event);
    }

    public void addEvent(Long userId, String eventType, String operation, Long entityId) {
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();

        addEvent(event);
    }

    public List<Event> getFeedByUserId(Long userId) {
        log.info("Запрошена лента событий для пользователя {}", userId);
        return feedStorage.findEventsByUserId(userId);

    }
}
