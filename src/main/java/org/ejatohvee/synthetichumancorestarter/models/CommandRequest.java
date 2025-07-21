package org.ejatohvee.synthetichumancorestarter.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommandRequest(
        @NotBlank(message = "Description must not be blank")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,
        @NotNull(message = "Priority must be specified")
        Priority priority,

        @NotBlank(message = "Author must not be blank")
        @Size(max = 100, message = "Author must not exceed 100 characters")
        String author,

        @NotBlank(message = "Time must not be blank")
        @Pattern(
                regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z$",
                message = "Time must be in ISO 8601 format, e.g., 2025-07-20T15:30:00Z")
        String time
) {
}
