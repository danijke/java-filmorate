package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.DateConstraint;

import java.time.*;
import java.util.*;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class Film {
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    List<Genre> genres;

    Rating mpa;

    @NotNull
    @DateConstraint(minDate = "1895-12-28", message = "Дата релиза не раньше 28 декабря 1895 года")
    LocalDate releaseDate;

    @Positive
    long duration;

}
