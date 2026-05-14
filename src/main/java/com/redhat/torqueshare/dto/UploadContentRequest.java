package com.redhat.torqueshare.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UploadContentRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(
            regexp = "^[a-zA-Z0-9-_]+$",
            message = "Invalid slug format"
    )
    private String originalWord; // The user's word

    @NotBlank
    private String contentType;  // e.g., "application/pdf"

    @Positive
    @Max(600_000_000)
    private long fileSize;       // in bytes

    @Size(max = 500)
    private String message;      // optional note
}