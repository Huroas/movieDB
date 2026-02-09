package codemen.task.movieDB.repository;

import codemen.task.movieDB.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {
    Optional<Review> findByMovie_MovieIdAndUsername(String movieId, String username);
    Page<Review> findByMovie_MovieId(String movieId, Pageable pageable);
}
