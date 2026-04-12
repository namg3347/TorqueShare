package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class DomainException extends RuntimeException{
    private final String errorCode;
    private final HttpStatus httpStatus;
    public DomainException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
