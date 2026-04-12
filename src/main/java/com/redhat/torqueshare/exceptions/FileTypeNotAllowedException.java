package com.redhat.torqueshare.exceptions;

import org.springframework.http.HttpStatus;

public class FileTypeNotAllowedException extends DomainException{
    public FileTypeNotAllowedException() {
        super("file type is not allowed,try pdf,png,jpeg etc", "FILE_TYPE_NOT_ALLOWED", HttpStatus.FORBIDDEN);
    }
}
