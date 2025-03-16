package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        userStorage.add(user);
        log.info("пользователь c логином {} успешно добавлен", user.getLogin());
        return user;
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
        return userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = get(userId);
        User otherUser = get(friendId);
        user.setFriend(friendId);
        otherUser.setFriend(userId);
        log.info("пользователь {} добавил в друзья пользователя {}", user.getName(), otherUser.getName());
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = get(userId);
        User otherUser = get(friendId);
        user.removeFriend(friendId);
        otherUser.removeFriend(userId);
        log.info("пользователь {} удалил из друзей пользователя {}", user.getName(), otherUser.getName());
    }

    public Collection<User> getFriends(Long userId) {
        return get(userId).getFriends()
                .filter(entry -> entry.getValue().equals(User.FriendStatus.CONFIRMED))
                .map(entry -> get(entry.getKey()))
                .collect(Collectors.toSet());
    }

    public Collection<User> getMutualFriends(Long userId, Long otherUserId) {
        Set<User> otherUserFriends = (Set<User>) (getFriends(otherUserId));
        otherUserFriends.retainAll(getFriends(userId));

        return otherUserFriends;
    }
}
//todo добавить проверку на уникальный email в валидацию в будущем