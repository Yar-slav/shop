package com.gridu.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Exceptions {

    TOKEN_NOT_FOUND("User already exist", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED("Token expired", HttpStatus.UNAUTHORIZED),
    ALREADY_CONFIRMED("Already confirmed", HttpStatus.ACCEPTED),
    USER_EXIST("Token not found", HttpStatus.CONFLICT),
    USER_NOT_FOUND("User is not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;
}
