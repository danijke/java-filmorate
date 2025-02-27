package ru.yandex.practicum.filmorate.exception;

import lombok.*;

@AllArgsConstructor
@Getter
public class ParameterNotValidException extends IllegalArgumentException {
    String parameter;
    String reason;
}
