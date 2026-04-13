package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class ContentTypeNotMatchingException extends DomainException{
    public ContentTypeNotMatchingException() {
        super("the content type doesnt match", "CONTENT_CORRUPTED", HttpStatus.CONFLICT);
    }
}
