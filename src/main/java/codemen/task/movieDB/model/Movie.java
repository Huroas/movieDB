package codemen.task.movieDB.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String Id;

    String title;       //Elokuvan nimi (merkkijono).
    String genre;       //Elokuvan genre (merkkijono, esim. "Sci-Fi", "Comedy", "Drama").
    int releaseYear;    //Elokuvan julkaisuvuosi (numero).
    String director;    //Elokuvan ohjaaja (merkkijono).
    int rating;         //Elokuvan arvio (numero, esim. 1–10).

    @Version
    private Long version;
}
