package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable Long userId) {
        return userService.get(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<String> addFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        return ResponseEntity.ok(userService.addFriend(userId, friendId));
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<String> deleteFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        return ResponseEntity.ok(userService.deleteFriend(userId, friendId));
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends() {
        return userService.getFriends();
    }

    @GetMapping("{userId}/friends/common/{otherId}")
    public Collection<User> getFriends(
            @PathVariable Long userId,
            @PathVariable Long otherId
    ) {
        return userService.getMutualFriends();
    }
}

//todo PUT /users/{id}/friends/{friendId} — добавление в друзья.
//todo DELETE /users/{id}/friends/{friendId} — удаление из друзей.
//todo GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
//todo GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем. (cравнить через стрим id в Set id между собой)