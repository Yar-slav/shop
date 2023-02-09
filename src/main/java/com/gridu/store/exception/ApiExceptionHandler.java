package com.gridu.store.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ApiExceptionHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleBadRequestException(MethodArgumentNotValidException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        ApiExceptionObject apiExceptionObject = getApiExceptionObject(httpStatus.toString(), errorMessages);
        return new ResponseEntity<>(apiExceptionObject, httpStatus);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionObject> httpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiExceptionObject apiExceptionObject = getApiExceptionObject(httpStatus.toString(), e.getMessage());
        return new ResponseEntity<>(apiExceptionObject, httpStatus);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiExceptionObject> handleException(ResponseStatusException e) {
        HttpStatusCode httpStatus = e.getStatusCode();
        String message = e.getReason();
        ApiExceptionObject apiExceptionObject = getApiExceptionObject(httpStatus.toString(), message);
        return new ResponseEntity<>(apiExceptionObject, httpStatus);
    }

    private ApiExceptionObject getApiExceptionObject(String httpStatus, String errorMessages) {
        return new ApiExceptionObject(
                httpStatus,
                errorMessages,
                LocalDateTime.now().format(formatter)
        );
    }
}
