package ru.practicum.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String object, Long id) {
        super(String.format("%s с id=%d не найден.", object, id));
    }
}