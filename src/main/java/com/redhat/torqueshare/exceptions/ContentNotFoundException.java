package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class ContentNotFoundException extends DomainException
{
    public ContentNotFoundException() {
        super("CONTENT_NOT_FOUND","content for this url is not found", HttpStatus.NOT_FOUND);
    }

}
