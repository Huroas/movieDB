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

            List<Movie> seeds = List.of(
                    movie("Conan", "Milius", "Adventure", 1986, 8),
                    movie("Titanic", "Cameron", "Drama", 1999, 7),
                    movie("Predator", "McTiernan", "Action", 1987, 8),
                    movie("The Matrix", "Wachowski", "SciFi", 1999, 9),
                    movie("Inception", "Nolan", "SciFi", 2010, 9),
                    movie("The Dark Knight", "Nolan", "Action", 2008, 9),
                    movie("Interstellar", "Nolan", "SciFi", 2014, 8),
                    movie("Memento", "Nolan", "Thriller", 2000, 8),
                    movie("Gladiator", "Scott", "Action", 2000, 8),
                    movie("Alien", "Scott", "SciFi", 1979, 9),
                    movie("Aliens", "Cameron", "SciFi", 1986, 9),
                    movie("Terminator 2", "Cameron", "Action", 1991, 9),
                    movie("The Terminator", "Cameron", "Action", 1984, 8),
                    movie("Blade Runner", "Scott", "SciFi", 1982, 8),
                    movie("Die Hard", "McTiernan", "Action", 1988, 9),
                    movie("The Godfather", "Coppola", "Crime", 1972, 10),
                    movie("The Godfather Part II", "Coppola", "Crime", 1974, 9),
                    movie("Pulp Fiction", "Tarantino", "Crime", 1994, 9),
                    movie("Reservoir Dogs", "Tarantino", "Crime", 1992, 8),
                    movie("Kill Bill Vol. 1", "Tarantino", "Action", 2003, 8),
                    movie("Kill Bill Vol. 2", "Tarantino", "Action", 2004, 8),
                    movie("The Shawshank Redemption", "Darabont", "Drama", 1994, 10),
                    movie("Fight Club", "Fincher", "Drama", 1999, 9),
                    movie("Se7en", "Fincher", "Thriller", 1995, 8),
                    movie("The Social Network", "Fincher", "Drama", 2010, 8),
                    movie("The Silence of the Lambs", "Demme", "Thriller", 1991, 9),
                    movie("The Usual Suspects", "Singer", "Crime", 1995, 8),
                    movie("The Departed", "Scorsese", "Crime", 2006, 8),
                    movie("Goodfellas", "Scorsese", "Crime", 1990, 9),
                    movie("Casino", "Scorsese", "Crime", 1995, 8),
                    movie("Taxi Driver", "Scorsese", "Drama", 1976, 8),
                    movie("Back to the Future", "Zemeckis", "SciFi", 1985, 9),
                    movie("Raiders of the Lost Ark", "Spielberg", "Adventure", 1981, 9),
                    movie("Jurassic Park", "Spielberg", "Adventure", 1993, 9),
                    movie("Jaws", "Spielberg", "Thriller", 1975, 8),
                    movie("E.T.", "Spielberg", "SciFi", 1982, 8),
                    movie("Saving Private Ryan", "Spielberg", "War", 1998, 9),
                    movie("Schindler's List", "Spielberg", "Drama", 1993, 9),
                    movie("The Lord of the Rings The Fellowship of the Ring", "Jackson", "Fantasy", 2001, 9),
                    movie("The Lord of the Rings The Two Towers", "Jackson", "Fantasy", 2002, 9),
                    movie("The Lord of the Rings The Return of the King", "Jackson", "Fantasy", 2003, 9),
                    movie("Star Wars A New Hope", "Lucas", "SciFi", 1977, 9),
                    movie("The Empire Strikes Back", "Kershner", "SciFi", 1980, 9),
                    movie("Return of the Jedi", "Marquand", "SciFi", 1983, 8),
                    movie("The Lion King", "Allers", "Animation", 1994, 8),
                    movie("Toy Story", "Lasseter", "Animation", 1995, 8),
                    movie("Finding Nemo", "Stanton", "Animation", 2003, 8),
                    movie("Up", "Docter", "Animation", 2009, 8),
                    movie("Spirited Away", "Miyazaki", "Animation", 2001, 9),
                    movie("Parasite", "Bong", "Drama", 2019, 9)
            );

            repository.saveAll(seeds);
        };
    }

    private Movie movie(String title, String director, String genre, Integer releaseYear, Integer rating) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDirector(director);
        movie.setGenre(genre);
        movie.setReleaseYear(releaseYear);
        movie.setRating(rating);
        return movie;
    }
}
