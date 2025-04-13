package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbc;
    private final RowMapper<Event> eventMapper;

    @Override
    public void saveEvent(Event event) {
        String sql = """
            INSERT INTO feed (timestamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)
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
        String sql = """
        SELECT * FROM feed
        WHERE user_id = ?
        ORDER BY timestamp ASC
    """;
        List<Event> events = jdbc.query(sql, eventMapper, userId);
        log.info("Лента событий для пользователя {}: {}", userId, events);
        return events;
    }
}
