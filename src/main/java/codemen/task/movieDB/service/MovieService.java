package codemen.task.movieDB.service;

import org.springframework.stereotype.Service;
import codemen.task.movieDB.model.Movie;
import codemen.task.movieDB.repository.MovieRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public List<Movie> getAllMovies() {
        return repository.findAll();
    }

    public List<Movie> getMoviesByTitle(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public Movie createMovie(Movie movie) {
        return repository.save(movie);
    }

    @Transactional
    public Movie updateMovie(String id, Movie movie) {
        Movie existing = repository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));

        existing.setTitle(movie.getTitle());
        existing.setGenre(movie.getGenre());
        existing.setReleaseYear(movie.getReleaseYear());
        existing.setDirector(movie.getDirector());
        existing.setRating(movie.getRating());
        return repository.save(existing);
    }

    @Transactional
    public void deleteMovie(String id) {
        repository.deleteById(id);
    }
}
