package com.pollub.cookie.exception;

public class DiscountCodeAlreadyExistsException extends RuntimeException {
    public DiscountCodeAlreadyExistsException(String message) {
        super(message);
    }
}
