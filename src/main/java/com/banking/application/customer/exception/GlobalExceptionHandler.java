
package com.banking.application.customer.exception;

import com.banking.application.auth.service.DuplicateBankingUserException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(
            DuplicateCustomerException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            CustomerNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Request contains invalid or missing fields"
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadableRequest(
            HttpMessageNotReadableException ex) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Request body is invalid"
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            DataIntegrityViolationException ex) {
        return problem(
                HttpStatus.CONFLICT,
                "The request conflicts with an existing database record"
        );
    }

    @ExceptionHandler(DuplicateBankingUserException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateBankingUser(
            DuplicateBankingUserException ex) {

        ProblemDetail body = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationFailure(
            AuthenticationException ex) {

        ProblemDetail body = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Invalid email or password");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatus status, String detail) {
        ProblemDetail body =
                ProblemDetail.forStatusAndDetail(status, detail);
        return ResponseEntity.status(status).body(body);
    }
}
