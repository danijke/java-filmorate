package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.DateConstraint;

import java.time.*;
import java.util.*;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class Film {
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    Genre genre;

    Rating rating;

    @NotNull
    @DateConstraint(minDate = "1895-12-28", message = "Дата релиза не раньше 28 декабря 1895 года")
    LocalDate releaseDate;

    @Positive
    long duration;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    Set<Long> likes;

    public Film(Long id, String name, String description, LocalDate releaseDate, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
    }

    public Set<Long> getLikes() {
        return Set.copyOf(likes);
    }

    public void setLike(Long userId) {
        likes.add(userId);
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
    }

    public static enum Genre {
        COMEDY,
        DRAMA,
        CARTOON,
        THRILLER,
        DOCUMENTARY,
        ACTION;
    }

    public static enum Rating {
        G,
        PG,
        PG_13,
        R,
        NC_17;
    }
}
