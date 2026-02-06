package codemen.task.movieDB.repository;

import codemen.task.movieDB.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, String> {
    @Query("""
    SELECT m FROM Movie m 
    WHERE (:id IS NULL OR m.movieId = :id)
      AND (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%')))
      AND (:year IS NULL OR m.releaseYear = :year)
      AND (:rating IS NULL OR m.rating >= :rating)
""")
    Page<Movie> searchMovies(
            @Param("id") String id,
            @Param("title") String title,
            @Param("genre") String genre,
            @Param("year") Integer year,
            @Param("rating") Integer rating,
            Pageable pageable);
}
