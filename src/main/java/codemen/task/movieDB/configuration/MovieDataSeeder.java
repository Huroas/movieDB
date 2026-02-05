package codemen.task.movieDB.configuration;

import codemen.task.movieDB.model.Movie;
import codemen.task.movieDB.repository.MovieRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MovieDataSeeder {

    @Bean
    ApplicationRunner seedMovies(MovieRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            Movie conan = new Movie();
            conan.setTitle("Conan");
            conan.setDirector("Milius");
            conan.setGenre("good");
            conan.setReleaseYear(1986);

            Movie titanic = new Movie();
            titanic.setTitle("Titanic");
            titanic.setDirector("Cameron");
            titanic.setGenre("");
            titanic.setReleaseYear(1999);

            Movie predator = new Movie();
            predator.setTitle("Predator");
            predator.setDirector("");
            predator.setGenre("Fun");
            predator.setReleaseYear(1987);

            repository.saveAll(List.of(conan, titanic, predator));
        };
    }
}
