package com.redhat.torqueshare.services;

import com.mongodb.DuplicateKeyException;
import com.redhat.torqueshare.entities.ProcessEvents;
import com.redhat.torqueshare.entities.UploadAnalytics;
import com.redhat.torqueshare.events.UploadCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;
    private static final String GLOBAL_ID = "Global";

    @KafkaListener(topics = "torque-share-kafka", groupId = "analytics-service")
    public void consume(UploadCompletedEvent event) {
        log.info("Received upload completed event in analysis-service");
        //IDEMPOTENCY
        try {
            mongoTemplate.insert(
                    ProcessEvents.builder().
                            eventId(event.getEventId()).
                            build()
            );
        } catch (DuplicateKeyException e) {
            log.info("Duplicate key found");
            return;
        }

        //ATOMIC(CONCURRENT) UPDATE

        Update update = new Update()
                //total upload update
                .inc("totalUploads", 1)
                .inc("totalSize", event.getExpectedSize());

        String type = event.getExpectedContentType();

        //image upload update
        if(type.startsWith("image/")) {
            update.inc("imageUploads", 1);
            update.inc("imageSize", event.getExpectedSize());
        }

        //file upload update
        else if(type.startsWith("application/")) {
            update.inc("fileUploads", 1);
            update.inc("fileSize", event.getExpectedSize());
        }

        mongoTemplate.upsert(
                new Query(where("_id").is(GLOBAL_ID)),
                update,
                UploadAnalytics.class
        );

        log.info("Analytics updated for slug={}", event.getSlug());
    }

}
