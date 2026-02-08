package codemen.task.movieDB.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "movies", indexes = {@Index(name = "idx_title", columnList = "title")})
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String movieId;

    @NotBlank(message = "Title is missing")
    String title;
    String genre;
    String director;
    Integer releaseYear;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating cannot be more than 10")
    Integer rating;

    @Version
    private Long version;
}
