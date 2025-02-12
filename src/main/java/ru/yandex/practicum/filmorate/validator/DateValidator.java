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
        if (date == null) {
            return false;
        }
        return !date.isBefore(minDate) && !date.isAfter(LocalDate.now());
    }
}
