package com.redhat.torqueshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UploadContentResponse {
    String slug;
    String uploadUrl;
    Instant expiryDate;
}
