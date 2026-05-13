package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class FileExpiredException extends DomainException {
    public FileExpiredException() {
        super("file had expired", "FILE_EXPIRED", HttpStatus.GONE);
    }
}
