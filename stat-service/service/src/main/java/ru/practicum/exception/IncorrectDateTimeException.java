package ru.practicum.exception;

public class IncorrectDateTimeException extends RuntimeException {

    public IncorrectDateTimeException(String message) {
        super(message);
    }
}
