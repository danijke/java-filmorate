package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

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
        User oldUser = userStorage.get(newUser.getId())
                .orElseThrow(() -> new NotFoundException("пользователь с id = " + newUser.getId() + " не найден"));

        boolean emailExits = userStorage.getAll().stream()
                .anyMatch(user -> user.getEmail().equals(newUser.getEmail()) &&
                        !user.getId().equals(newUser.getId()));
        if (emailExits) {
            throw new ValidationException("Этот имейл уже используется");
        }

        oldUser.setEmail(newUser.getEmail());
        oldUser.setName(newUser.getName());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());
        log.info("пользователь c логином {} успешно обновлен", oldUser.getLogin());
        return oldUser;
    }

    public Collection<User> findAll() {
        return userStorage.getAll();
    }
}
