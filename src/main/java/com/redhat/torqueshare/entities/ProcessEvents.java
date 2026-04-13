package com.redhat.torqueshare.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "process_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessEvents {
    @Id
    private String eventId;
}
