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
    private final UserStorage userStorage;

    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.saveUser(user).orElseThrow(() ->
                new NotFoundException("ошибка при получении сохраненного пользователя из бд: " + user.getLogin()));
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("id должен быть указан");
        }
        return userStorage.update(newUser).orElseThrow(() ->
                new NotFoundException("пользователь с id = " + newUser.getId() + " не найден"));
    }

    public void delete(Long userId) {
        userStorage.removeUser(userId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User get(Long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь с id = " + userId + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        checkUsersExist(userId, friendId);
        userStorage.addFriend(userId, friendId);
        log.info("пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        checkUsersExist(userId, friendId);
        userStorage.deleteFriend(userId, friendId);
        log.info("пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        return userStorage.getAllFriends(get(userId).getId());
    }

    public Collection<User> getJointFriends(Long userId, Long friendId) {
        return userStorage.getJointFriends(userId, friendId);
    }

    public void checkUsersExist(Long... userIds) {
        if (!userStorage.containsUsersByIds(userIds)) {
            throw new NotFoundException(
                    String.format("пользователи c id : %s не найдены", Arrays.toString(userIds))
            );
        }
    }
}
//todo добавить проверку на уникальный email в валидацию в будущем