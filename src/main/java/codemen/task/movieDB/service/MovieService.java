package codemen.task.movieDB.service;

import codemen.task.movieDB.model.Movie;
import codemen.task.movieDB.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public Page<Movie> getAllMovies(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Movie getMovieById(String id){
        return repository.findById(id).orElse(null);
    }

    public Page<Movie> searchMovies(String id, String title, String genre, Integer year, Integer rating, Pageable pageable) {
        return repository.searchMovies(normalize(id), normalize(title), normalize(genre), year, rating, pageable);
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

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
