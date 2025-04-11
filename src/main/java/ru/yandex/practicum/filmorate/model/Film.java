package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.DateConstraint;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    List<Genre> genres;

    Set<Director> directors;
    @NotNull(message = "У фильма должен быть указан рейтинг MPA")
    Rating mpa;

    @NotNull
    @DateConstraint(minDate = "1895-12-28", message = "Дата релиза не раньше 28 декабря 1895 года")
    LocalDate releaseDate;

    @Positive
    long duration;

}
