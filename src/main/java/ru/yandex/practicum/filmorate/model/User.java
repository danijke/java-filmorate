package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.DateConstraint;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Неверный формат электронной почты")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^[^\\s]+$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @NotNull
    @DateConstraint(minDate = "1900-01-01", message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Map<Long, FriendStatus> friends;

    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashMap<>();
    }

    public void setFriend(Long friendId) {
        friends.put(friendId, FriendStatus.CONFIRMED);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    public Stream<Map.Entry<Long, FriendStatus>> getFriends() {
        return friends.entrySet().stream();
    }

    public static enum FriendStatus {
        REQUESTED,
        CONFIRMED,
        NOT_CONFIRMED;
    }
}
