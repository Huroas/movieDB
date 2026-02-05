package codemen.task.movieDB.controller;

import codemen.task.movieDB.model.Movie;
import codemen.task.movieDB.service.MovieService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {
    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<Movie> getMovies() {
        return service.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getOne(@PathVariable String id) {
        return service.getMovieById(id);
    }

    @PostMapping
    public Movie create(@RequestBody Movie product) {
        return service.createMovie(product);
    }

    @PutMapping("/{id}")
    public Movie update(@PathVariable String id, @RequestBody Movie movie) {
        return service.updateMovie(id, movie);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteMovie(id);
    }
}
