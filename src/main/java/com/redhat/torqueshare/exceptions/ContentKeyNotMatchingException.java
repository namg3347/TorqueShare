package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class ContentKeyNotMatchingException extends DomainException{
    public ContentKeyNotMatchingException() {
        super("s3 key not matching", "CORRUPTED_KEY", HttpStatus.CONFLICT);
    }
}
