package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class RateLimitException extends  DomainException{
    public RateLimitException() {
        super("too many requests", "RATE_LIMITED", HttpStatus.TOO_MANY_REQUESTS);
    }
}
