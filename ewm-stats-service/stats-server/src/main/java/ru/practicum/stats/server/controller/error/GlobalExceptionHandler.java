package ru.practicum.stats.server.controller.error;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.stats.server.exception.ConditionsNotMetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Map<String, List<String>>> handleValidationException(Exception ex) {
        BindingResult bindingResult;

        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException manve = (MethodArgumentNotValidException) ex;
            bindingResult = manve.getBindingResult();
        } else {
            BindException be = (BindException) ex;
            bindingResult = be.getBindingResult();
        }

        Map<String, List<String>> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            String field = error.getField();
            List<String> messages = errors.get(field);
            if (messages == null) {
                messages = new ArrayList<>();
                errors.put(field, messages);
            }
            messages.add(error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, List<String>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        errors.put(ex.getName(), List.of("Invalid value: " + ex.getValue()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, List<String>>> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        errors.put(ex.getParameterName(), List.of("Parameter is required"));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    public ResponseEntity<Map<String, List<String>>> handleConditionsNotMetException(ConditionsNotMetException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        errors.put("error", List.of(ex.getMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
