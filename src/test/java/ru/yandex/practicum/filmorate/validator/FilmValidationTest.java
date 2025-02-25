package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Валидатор фильмов")
public class FilmValidationTest extends BaseValidationTest {

    @Test
    @DisplayName("должен пройти валидацию")
    void shouldPassWithValidFilm() {
        Film film = new Film(0L,"Valid Name",  "A".repeat(200), LocalDate.of(1895, 12, 28),1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("должен провалить валидацию")
    void shouldFailWithNotValidFilm() {
        Film film = new Film(0L,"",  "A".repeat(201), LocalDate.of(1895, 12, 27),0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).hasSize(4);
    }
}
