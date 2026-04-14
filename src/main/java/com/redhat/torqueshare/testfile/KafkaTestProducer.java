package com.redhat.torqueshare.testfile;

import com.redhat.torqueshare.events.UploadCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaTestProducer {

    private final KafkaTemplate<String, UploadCompletedEvent> kafkaTemplate;

    private static final String TOPIC = "torque-share-kafka";

    public void sendTestEvent() {

        String slug = "test-slug"; // simulate your slug
        String s3Key = "uploads/" + slug;

        UploadCompletedEvent event = new UploadCompletedEvent(
                UUID.randomUUID().toString(),
                s3Key, slug, 10L,"image/png"
        );

        kafkaTemplate.send(TOPIC, slug, event).whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Failed: " + ex.getMessage());
            } else {
                System.out.println("Sent to partition: " +
                        result.getRecordMetadata().partition());
            }
        });
        System.out.println("Sent event: " + event);
    }
}