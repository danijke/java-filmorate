package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface UserStorage {
    void add(User user);

    Optional<User> get(Long id);

    Collection<User> getAll();
}
