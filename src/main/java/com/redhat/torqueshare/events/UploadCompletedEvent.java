package com.redhat.torqueshare.events;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadCompletedEvent {
    private String eventId; //UUID
    private String s3Key;
    private String slug;
}