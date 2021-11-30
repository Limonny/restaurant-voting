package com.example.restaurantvoting.exception;

import lombok.Getter;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@Getter
public class VoteSubmissionException extends ResponseStatusException {

    private final ErrorAttributeOptions options;

    public VoteSubmissionException(HttpStatus status, String message) {
        super(status, message);
        this.options = ErrorAttributeOptions.of(MESSAGE);
    }
}