package com.redhat.torqueshare.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UploadCompletedEvent {
    private String eventId; //uuid
    private String s3Key;
    private String slug;
}