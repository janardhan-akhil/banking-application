package com.banking.application.exception;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException(String email) {
        super("A customer with this email already exists");
    }
}