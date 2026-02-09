package codemen.task.movieDB.controller;

import codemen.task.movieDB.dto.PageResponse;
import codemen.task.movieDB.dto.ReviewRequest;
import codemen.task.movieDB.dto.ReviewResponse;
import codemen.task.movieDB.model.Movie;
import codemen.task.movieDB.model.Review;
import codemen.task.movieDB.service.ReviewService;
import codemen.task.movieDB.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {
    private final MovieService service;
    private final ReviewService reviewService;

    public MovieController(MovieService service, ReviewService reviewService) {
        this.service = service;
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable String id) {
        return service.getMovieById(id);
    }

    @GetMapping
    public PageResponse<Movie> getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Movie> result = service.getAllMovies(pageRequest(page, size));
        return PageResponse.from(result);
    }

    @GetMapping("/search")
    public PageResponse<Movie> searchMovies(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Movie> result = service.searchMovies(id, title, genre, releaseYear, rating, pageRequest(page, size));
        return PageResponse.from(result);
    }

    @PostMapping
    public Movie create(@Valid @RequestBody Movie movie) {
        return service.createMovie(movie);
    }

    @PutMapping("/{id}")
    public Movie update(@PathVariable String id, @Valid @RequestBody Movie movie) {
        return service.updateMovie(id, movie);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteMovie(id);
    }

    @PostMapping("/{id}/reviews")
    public ReviewResponse saveReview(
            @PathVariable String id,
            @Valid @RequestBody ReviewRequest request,
            Principal principal) {
        Review review = reviewService.saveReview(id, principal.getName(), request.review());
        return new ReviewResponse(
                review.getReviewId(),
                review.getMovie().getMovieId(),
                review.getUsername(),
                review.getReviewText()
        );
    }

    @GetMapping("/{id}/reviews")
    public PageResponse<ReviewResponse> getReviews(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<ReviewResponse> result = reviewService.getReviews(id, reviewPageRequest(page, size))
                .map(this::toReviewResponse);
        return PageResponse.from(result);
    }

    private PageRequest pageRequest(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(safePage, safeSize, Sort.by("title").ascending());
    }

    private PageRequest reviewPageRequest(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 20);
        return PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());
    }

    private ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getMovie().getMovieId(),
                review.getUsername(),
                review.getReviewText()
        );
    }

}
