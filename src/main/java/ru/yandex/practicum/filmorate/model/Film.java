package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.DateConstraint;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = {"name"})
public class Film {
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    @DateConstraint(minDate = "1895-12-28", message = "Дата релиза не раньше 28 декабря 1895 года")
    LocalDate releaseDate;

    @Positive
    long duration;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    Set<Long> likes = new HashSet<>();

    public void setLike(Long userId) {
        likes.add(userId);
    }

    public void deleteLike(Long userId) {
        likes.remove(userId);
    }
}
