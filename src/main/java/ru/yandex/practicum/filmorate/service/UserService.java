package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final  UserStorage userStorage;

    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        return userStorage.saveUser(user)
                .map(u -> {
                    log.info("пользователь c логином {} успешно добавлен", user.getLogin());
                    return u;
                })
                .orElseThrow(() -> new NotFoundException(
                        String.format("Ошибка при получении сохраненного пользователя с логином = %s", user.getLogin())
                ));
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        get(newUser.getId());

        userStorage.getAll().stream()
                .filter(user -> user.getEmail().equals(newUser.getEmail()) && !user.getId().equals(newUser.getId()))
                .findAny()
                .ifPresent(user -> {
                    throw new ValidationException(user.getEmail() + " email уже используется");
                });

        return userStorage.update(newUser)
                .map(oldUser -> {
                    log.info("пользователь c логином {} успешно обновлен", oldUser.getLogin());
                    return oldUser;
                })
                .orElseThrow(() -> new NotFoundException("пользователь с id = " + newUser.getId() + " не найден"));
    }

    public Collection<User> findAll() {
        return userStorage.getAll();
    }

    public User get(Long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
        log.info("пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
        log.info("пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        return userStorage.getAllFriends(userId);
    }

    public Collection<User> getJointFriends(Long userId, Long friend_id) {
        return userStorage.getJointFriends(userId, friend_id);
    }
}
//todo добавить проверку на уникальный email в валидацию в будущем