package com.erp.common.exception;

/**
 * Thrown for recoverable, user-facing business rule violations.
 * Surfaced as a flash error message rather than a 500 page.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
