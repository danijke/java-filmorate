package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {
    void saveEvent(Event event);

    List<Event> findEventsByUserId(Long userId);
}
