package com.banking.application.customer.exception;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException(String email) {
        super("A customer with this email already exists");
    }
}