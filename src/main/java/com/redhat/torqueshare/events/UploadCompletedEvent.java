package com.redhat.torqueshare.events;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadCompletedEvent {
    private String s3Key;
    private String slug;
    private long expectedSize;
    private String expectedContentType;
}