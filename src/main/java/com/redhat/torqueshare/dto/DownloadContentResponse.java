package com.redhat.torqueshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadContentResponse {

    private String downloadUrl;

    private String message;
}
