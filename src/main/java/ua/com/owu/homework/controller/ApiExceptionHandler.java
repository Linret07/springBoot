package ua.com.owu.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Validation failed");
        body.put("details", exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(this::property, ConstraintViolation::getMessage,
                        (first, ignored) -> first, LinkedHashMap::new)));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Validation failed");
        body.put("details", exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage(),
                        (first, ignored) -> first, LinkedHashMap::new)));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String property(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int separator = path.lastIndexOf('.');
        return separator >= 0 ? path.substring(separator + 1) : path;
    }
}
