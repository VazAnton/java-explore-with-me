package ru.practicum.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.IncorrectDataException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> onEntityNotFoundException(final EntityNotFoundException e) {
        return Map.of(
                "Объект не найден.", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> onBadRequestException(final BadRequestException e) {
        return Map.of(
                "Допущена ошибка в запросе.", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> onIncorrectDataException(final IncorrectDataException e) {
        return Map.of(
                "Неверно указаны данные.", e.getMessage()
        );
    }
}
