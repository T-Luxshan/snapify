package com.luxshan.linkshort.linkshort.exception;

import com.luxshan.linkshort.linkshort.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LinkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleLinkNotFound(LinkNotFoundException linkNotFoundException){
        return ErrorResponse.builder()
                .error(linkNotFoundException.toString())
                .message(linkNotFoundException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
