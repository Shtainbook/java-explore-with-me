package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorEntity> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .status(BAD_REQUEST)
                .reason("Некорректный запрос.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorEntity> handleServerWebInputException(final ServerWebInputException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .message(e.getMessage())
                .reason(e.getReason())
                .status(BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorEntity> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .message(e.getMessage())
                .reason(NestedExceptionUtils.getMostSpecificCause(e).getMessage())
                .status(BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleValidationException(ValidationRequestException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .status(BAD_REQUEST)
                .reason("Некорректный запрос.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }

    // 404
    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ErrorEntity> handleNoSuchElementException(final NoSuchElementException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()

                .status(NOT_FOUND)
                .reason("Объект не найден.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorEntity> handleNotFound(final NotFoundException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .status(NOT_FOUND)
                .reason("Запрашиваемый объект не найден.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.NOT_FOUND);
    }

    // 409
    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleConflictException(ConflictException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .status(CONFLICT)
                .reason("Не выполнены условия для действия.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NonTransientDataAccessException.class)
    public ResponseEntity<ErrorEntity> handleNonTransientDataAccessException(final NonTransientDataAccessException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .message(e.getCause().getMessage())
                .reason(NestedExceptionUtils.getMostSpecificCause(e).getMessage())
                .status(CONFLICT)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorEntity> handleException(final Exception e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .status(INTERNAL_SERVER_ERROR)
                .reason("Произошла непредвиденная ситуация, из-за которой запрос не выполнен.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorEntity> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Произошла ошибка." + e.getMessage());
        return new ResponseEntity<>(ErrorEntity.builder()
                .status(BAD_REQUEST)
                .reason("Запрос сделан некорректно.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }
}