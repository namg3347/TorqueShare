package com.redhat.torqueshare.exceptions;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiErrorResponse {

    private Instant timestamp;

    private int status;

    private String error;

    private String message;
}