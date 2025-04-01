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
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .login("validLogin")
                .name("valid name")
                .birthday(LocalDate.of(2000,1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("должен провалить валидацию")
    void shouldFailWithNotValidUser() {
        User user = User.builder()
                .id(0L)
                .email("invalid-email")
                .login("invalid login")
                .name("valid name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(3);
    }
}
