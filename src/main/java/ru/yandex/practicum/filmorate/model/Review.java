package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    Long id;

    @NotNull
    String content;

    @NotNull
    boolean isPositive;

    @NotNull
    @Positive
    Long userId;

    @NotNull
    @Positive
    Long filmId;

    int rating;
}
