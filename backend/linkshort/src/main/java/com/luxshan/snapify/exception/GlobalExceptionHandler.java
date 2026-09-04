package com.luxshan.snapify.exception;

import com.luxshan.snapify.dto.ErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
                .error("LINK_NOT_FOUND")
                .message(linkNotFoundException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidUrl(MethodArgumentNotValidException exception){
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Invalid request");

        return ErrorResponse.builder()
                .error("Validation Error")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ShortCodeGenerationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleShortCodeGenerationException(ShortCodeGenerationException exception){
        return ErrorResponse.builder()
                .error("Short Code Generation Failed")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(LinkExpiredException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleLinkExpiredException(LinkExpiredException exception){
        return ErrorResponse.builder()
                .error("LINK_EXPIRED")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
