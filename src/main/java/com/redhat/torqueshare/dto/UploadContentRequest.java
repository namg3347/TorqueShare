package com.redhat.torqueshare.dto;

import lombok.Data;

@Data
public class UploadContentRequest {
    private String originalWord; // The user's word
    private String contentType;  // e.g., "application/pdf"
    private long fileSize;       // in bytes
    private String message;      // optional note
}