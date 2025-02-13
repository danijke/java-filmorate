package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.*;

import java.lang.annotation.*;

@Constraint(validatedBy = DateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateConstraint {
    String message() default "Дата не соответствует требованиям";
    String minDate();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
