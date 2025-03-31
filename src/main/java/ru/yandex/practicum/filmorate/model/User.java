package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.DateConstraint;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
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
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
