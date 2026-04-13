package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class ContentSizeNotMatchingException extends DomainException{
    public ContentSizeNotMatchingException() {
        super("the upload content size doesnt match with server", "CONTENT_CORRUPTED", HttpStatus.CONFLICT);
    }
}
