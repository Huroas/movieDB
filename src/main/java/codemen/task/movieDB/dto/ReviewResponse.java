package codemen.task.movieDB.dto;

public record ReviewResponse(
        String reviewId,
        String movieId,
        String username,
        String review
) {
}
