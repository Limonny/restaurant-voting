package com.example.restaurantvoting.exception;

import lombok.Getter;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class IdValidationException extends ResponseStatusException {

    private final ErrorAttributeOptions options;

    public IdValidationException(HttpStatus status, String message, ErrorAttributeOptions options) {
        super(status, message);
        this.options = options;
    }
}