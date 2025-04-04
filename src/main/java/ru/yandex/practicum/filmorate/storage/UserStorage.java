package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface UserStorage {
    Optional<User> saveUser(User user);

    void removeUser(Long id);

    Optional<User> findUserById(Long id);

    Collection<User> findAll();

    Optional<User> update(User newUser);

    boolean addFriend(Long userId, Long friendId);

    boolean deleteFriend(Long userId, Long friendId);

    Collection<User> getAllFriends(Long userId);

    Collection<User> getJointFriends(Long userId, Long friendId);

    boolean containsUserByIds(Long userId, Long friendId);
}
