package com.luxshan.linkshort.linkshort.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
