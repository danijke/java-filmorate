package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserRecommendationService;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FeedService feedService;
    private final UserRecommendationService userRecommendationService;

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
        log.trace("пользователь {} прошел валидацию и будет добавлен", user.getLogin());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends(
            @PathVariable Long userId
    ) {
        return userService.getFriends(userId);
    }

    @GetMapping("{userId}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(
            @PathVariable Long userId,
            @PathVariable Long otherId
    ) {
        return userService.getJointFriends(userId, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable Long id) {
        return feedService.getFeedByUserId(id);
    }

    @GetMapping("{userId}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable Long userId) {
       return userRecommendationService.getRecommendations(userId);
    }
}