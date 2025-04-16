package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Валидатор фильмов")
public class FilmValidationTest extends BaseValidationTest {

    @Test
    @DisplayName("должен пройти валидацию")
    void shouldPassWithValidFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("Valid Name")
                .description("A".repeat(200))
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .mpa(new Rating(1L,"G"))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("должен провалить валидацию")
    void shouldFailWithNotValidFilm() {
        Film film = Film.builder()
                .id(0L)
                .name("")
                .description("A".repeat(201))
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).hasSize(5);
    }
}
