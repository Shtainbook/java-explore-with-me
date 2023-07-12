package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> statusExceptionHandler(final ResponseStatusException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(Map.of(e.getClass(), Objects.requireNonNull(e.getReason())), e.getStatus());
    }
}