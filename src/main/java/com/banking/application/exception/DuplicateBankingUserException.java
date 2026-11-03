package com.banking.application.exception;

public class DuplicateBankingUserException extends RuntimeException {

    public DuplicateBankingUserException() {
        super("An account with this email already exists");
    }
}