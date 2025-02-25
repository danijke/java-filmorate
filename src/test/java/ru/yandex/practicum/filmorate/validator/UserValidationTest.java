package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserValidationTest extends BaseValidationTest {

    @Test
    @DisplayName("должен пройти валидацию")
    void shouldPassWithValidUser() {
        User user = new User(0L, "test@example.com", "validLogin", "valid name", LocalDate.of(2000,1,1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("должен провалить валидацию")
    void shouldFailWithNotValidUser() {
        User user = new User(0L, "invalid-email", "invalid login", "valid name", LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(3);
    }
}
