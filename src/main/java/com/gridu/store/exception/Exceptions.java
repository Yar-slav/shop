package com.gridu.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// Optional: I'd say that it's not the best approach
// Every time you need to add some exception, you need to go to this file and modify this enum
// It violates open-closed principle and leads to merging conflicts if you're working in a team, and you need to
// create different exceptions in different branches
// I'd suggest for each exception to create a separate exception type which extends ResponseStatusException
// Inside it, you may define HttpStatus and message as you wish
@Getter
@RequiredArgsConstructor
public enum Exceptions{

    ORDER_NOT_FOUND("Order not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_BELONG_USER("This number of order does not belong to you", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("Product not found", HttpStatus.NOT_FOUND),
    // Optional: That's not an appropriate status for such case
    PRODUCTS_NOT_ENOUGH("Amount of products not enough", HttpStatus.FORBIDDEN),
    USER_EXIST("User with this email already exist", HttpStatus.CONFLICT),
    // Optional: The correct status code for this scenario is 401, not 403
    // See: https://www.rfc-editor.org/rfc/rfc7235#section-3.1
    USER_INCORRECT_PASSWORD("Incorrect password", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("User is not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;
}
