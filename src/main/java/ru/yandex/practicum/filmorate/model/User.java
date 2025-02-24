package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.DateConstraint;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"login"})
public class User {
    Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Неверный формат электронной почты")
    String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^[^\\s]+$", message = "Логин не должен содержать пробелы")
    String login;

    String name;

    @DateConstraint(minDate = "1900-01-01", message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    @Builder.Default
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    Set<Long> friends = new HashSet<>();

    public void setFriend(Long friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Long friendId) {
        friends.remove(friendId);
    }

    public Set<Long> getFriends() {
        return Set.copyOf(friends);
    }
}
