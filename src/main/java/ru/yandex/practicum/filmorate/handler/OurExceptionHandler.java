package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class OurExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> dealWithNotFoundException(NotFoundException e) {
        Map<String, String> response = new HashMap<>();
        log.error("error", e.getMessage());
        response.put("error:", e.getMessage());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> dealWithValidationException(ValidationException e) {
        Map<String, String> response = new HashMap<>();
        log.error("error", e.getMessage());
        response.put("error:", e.getMessage());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> dealWithAnyException(Throwable e) {
        Map<String, String> response = new HashMap<>();
        log.error("error", e.getMessage());
        response.put("error:", e.getMessage());
        return response;
    }
}
