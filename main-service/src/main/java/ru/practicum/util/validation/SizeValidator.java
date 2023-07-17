package ru.practicum.util.validation;

import ru.practicum.exceptions.ValidationRequestException;

public class SizeValidator {
    public static void validateSize(Integer size) {
        if (size == 0) {
            throw new IllegalArgumentException("Ноль у size - не размер!");
        } else if (size < 0) {
            throw new ValidationRequestException("Значение size - меньше нуля");
        }
    }

    public static void validateSizeAndFrom(Integer size, Integer from) {
        if (size == 0) {
            throw new IllegalArgumentException("Ноль у size - не размер!");
        } else if (size < 0) {
            throw new ValidationRequestException("Значение size - меньше нуля");
        } else if (from < 0) {
            throw new ValidationRequestException("Значение from меньше нуля");
        }
    }
}