package com.banking.application.auth.service;

public class DuplicateBankingUserException extends RuntimeException {

    public DuplicateBankingUserException() {
        super("An account with this email already exists");
    }
}