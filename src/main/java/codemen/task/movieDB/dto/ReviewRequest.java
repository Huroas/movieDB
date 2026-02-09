package codemen.task.movieDB.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
        @NotBlank(message = "Review is missing") String review
) {
}
