package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.*;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<DateConstraint, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(DateConstraint constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.minDate());
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return !date.isBefore(minDate);
    }
}
