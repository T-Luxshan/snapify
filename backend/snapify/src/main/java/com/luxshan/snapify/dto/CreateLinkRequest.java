package com.luxshan.snapify.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateLinkRequest {

    @NotBlank(message = "Original URL is required")
    @URL(message = "Invalid URL format")
    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    @Pattern(
            regexp = "^https?://.+",
            message = "URL must start with http:// or https://"
    )
    private String originalUrl;

    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expiresAt;
}

