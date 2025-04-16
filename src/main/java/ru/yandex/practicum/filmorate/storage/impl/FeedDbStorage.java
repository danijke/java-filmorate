package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final UserStorage userStorage;
    private final JdbcTemplate jdbc;
    private final RowMapper<Event> eventMapper;

    @Override
    public void saveEvent(Event event) {
        String sql = """
                    INSERT INTO feed (timestamp, user_id, event_type_id, operation_id, entity_id)
                    VALUES (?, ?,
                        (SELECT event_type_id FROM event_types WHERE name = ?),
                        (SELECT operation_id FROM operations WHERE name = ?),
                        ?)
                """;

        jdbc.update(sql,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId()
        );
    }

    @Override
    public List<Event> findEventsByUserId(Long userId) {
        if (!userStorage.containsUsersByIds(userId)) {
            throw new NotFoundException(
                    String.format("пользователь c id : %s не найден", userId)
            );
        }
        String sql = """
        SELECT f.event_id, f.timestamp, f.user_id,
               et.name AS event_type,
               op.name AS operation,
               f.entity_id
        FROM feed f
        JOIN event_types et ON f.event_type_id = et.event_type_id
        JOIN operations op ON f.operation_id = op.operation_id
        WHERE f.user_id = ?
        ORDER BY f.timestamp ASC
    """;

        List<Event> events = jdbc.query(sql, eventMapper, userId);
        log.info("Лента событий для пользователя {}: {}", userId, events);
        return events;
    }

}
