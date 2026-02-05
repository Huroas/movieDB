package codemen.task.movieDB.repository;

import codemen.task.movieDB.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {

}
