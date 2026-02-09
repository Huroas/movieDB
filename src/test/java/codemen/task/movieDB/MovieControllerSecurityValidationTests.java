package codemen.task.movieDB;

import codemen.task.movieDB.model.Movie;
import codemen.task.movieDB.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite:target/test.db",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MovieControllerSecurityValidationTests {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    private String basicAuthHeader() {
        String token = Base64.getEncoder()
                .encodeToString("admin:password".getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }

    @Test
    void createMovie_requiresAuth() throws Exception {
        Map<String, Object> payload = Map.of("title", "Test movie");

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createMovie_blankTitle_returnsValidationError() throws Exception {
        Map<String, Object> payload = Map.of("title", "");

        mockMvc.perform(post("/api/movies")
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is missing"));
    }

    @Test
    void createMovie_invalidRating_returnsValidationError() throws Exception {
        Map<String, Object> payload = Map.of(
                "title", "Test movie",
                "rating", 11
        );

        mockMvc.perform(post("/api/movies")
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.rating").value("Rating cannot be more than 10"));
    }

    @Test
    void updateMovie_requiresAuth() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Original title");
        Movie saved = movieRepository.save(movie);

        Map<String, Object> payload = Map.of("title", "Updated title");

        mockMvc.perform(put("/api/movies/{id}", saved.getMovieId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMovie_blankTitle_returnsValidationError() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Original title");
        Movie saved = movieRepository.save(movie);

        Map<String, Object> payload = Map.of("title", "");

        mockMvc.perform(put("/api/movies/{id}", saved.getMovieId())
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is missing"));
    }

    @Test
    void updateMovie_invalidRating_returnsValidationError() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Original title");
        Movie saved = movieRepository.save(movie);

        Map<String, Object> payload = Map.of(
                "title", "Updated title",
                "rating", 0
        );

        mockMvc.perform(put("/api/movies/{id}", saved.getMovieId())
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.rating").value("Rating must be at least 1"));
    }

    @Test
    void createReview_blankText_returnsValidationError() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Review target");
        Movie saved = movieRepository.save(movie);

        Map<String, Object> payload = Map.of("review", "");

        mockMvc.perform(post("/api/movies/{id}/reviews", saved.getMovieId())
                        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.review").value("Review is missing"));
    }
}
