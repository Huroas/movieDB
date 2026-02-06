package codemen.task.movieDB.model;

import jakarta.persistence.*;
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
    Integer rating;

    @Version
    private Long version;
}
