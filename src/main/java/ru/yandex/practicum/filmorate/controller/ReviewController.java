package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{reviewId}")
    public void delete(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review get(@PathVariable Long reviewId) {
        return reviewService.get(reviewId);
    }

    @GetMapping()
    public Collection<Review> getSortedByUseful(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count
    ) {
        return reviewService.getSortedByUseful(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void setLike(
            @PathVariable Long reviewId,
            @PathVariable Long userId
    ) {
        reviewService.setLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void setDislike(
            @PathVariable Long reviewId,
            @PathVariable Long userId
    ) {
        reviewService.setDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void deleteLike(
            @PathVariable Long reviewId,
            @PathVariable Long userId
    ) {
        reviewService.deleteReaction(reviewId, userId);
    }
}
