package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class FileTooLargeException extends DomainException {

    public FileTooLargeException() {
        super("Can't upload large files", "FILE_TOO_LARGE", HttpStatus.BAD_REQUEST);
    }
}
