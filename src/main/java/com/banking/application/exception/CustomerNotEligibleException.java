package com.banking.application.exception;

public class CustomerNotEligibleException extends RuntimeException{
    public CustomerNotEligibleException(String message) {
        super(message);
    }
}
