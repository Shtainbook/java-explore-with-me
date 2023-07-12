package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorEntity> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .status(BAD_REQUEST)
                .reason("Некорректный запрос.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NonTransientDataAccessException.class})
    public ResponseEntity<ErrorEntity> handleNonTransientDataAccessException(final NonTransientDataAccessException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .message(e.getCause().getMessage())
                .reason(NestedExceptionUtils.getMostSpecificCause(e).getMessage())
                .status(CONFLICT)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ServerWebInputException.class})
    public ResponseEntity<ErrorEntity> handleServerWebInputException(final ServerWebInputException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .message(e.getMessage())
                .reason(e.getReason())
                .status(BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleValidationException(ValidationRequestException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .status(BAD_REQUEST)
                .reason("екорректный запрос.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }
}