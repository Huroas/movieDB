package codemen.task.movieDB.service;

import codemen.task.movieDB.model.Movie;
import codemen.task.movieDB.model.Review;
import codemen.task.movieDB.repository.MovieRepository;
import codemen.task.movieDB.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public Review saveReview(String movieId, String username, String reviewText) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        Review review = reviewRepository.findByMovie_MovieIdAndUsername(movieId, username)
                .orElseGet(Review::new);

        review.setMovie(movie);
        review.setUsername(username);
        review.setReviewText(reviewText);
        return reviewRepository.save(review);
    }

    public Page<Review> getReviews(String movieId, Pageable pageable) {
        return reviewRepository.findByMovie_MovieId(movieId, pageable);
    }
}
