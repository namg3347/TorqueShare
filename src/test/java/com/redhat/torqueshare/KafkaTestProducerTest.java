package com.redhat.torqueshare;

import com.redhat.torqueshare.events.UploadCompletedEvent;
import com.redhat.torqueshare.testfile.KafkaTestProducer;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class KafkaTestProducerTest {

    //@Test
    void shouldSendUploadEvent() {

        // Arrange
        KafkaTemplate<String, UploadCompletedEvent> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaTestProducer producer = new KafkaTestProducer(kafkaTemplate);

        // Act
        producer.sendTestEvent();

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UploadCompletedEvent> valueCaptor = ArgumentCaptor.forClass(UploadCompletedEvent.class);

        verify(kafkaTemplate, times(1))
                .send(eq("torque-share-kafka"), keyCaptor.capture(), valueCaptor.capture());

        String key = keyCaptor.getValue();
        UploadCompletedEvent event = valueCaptor.getValue();

        assert key.equals("test-slug");
        assert event.getEventId() != null;
        assert event.getS3Key().contains("uploads/test-slug");
    }
}