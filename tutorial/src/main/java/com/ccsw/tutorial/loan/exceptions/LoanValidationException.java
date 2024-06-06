package com.ccsw.tutorial.loan.exceptions;

public class LoanValidationException extends RuntimeException {
    public LoanValidationException(String message) {
        super(message);
    }
}