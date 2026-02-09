package codemen.task.movieDB.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "movie_reviews", indexes = {
        @Index(name = "idx_review_movie", columnList = "movie_id"),
        @Index(name = "idx_review_user", columnList = "username")
})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private String username;

    @Column(name = "review_text", nullable = false, length = 1000)
    private String reviewText;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
