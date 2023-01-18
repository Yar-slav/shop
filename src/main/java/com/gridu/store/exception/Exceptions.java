package com.gridu.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Exceptions{
    USER_EXIST("User with this email already exist", HttpStatus.CONFLICT),
    USER_INCORRECT_PASSWORD("Incorrect password", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("User is not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;
}
