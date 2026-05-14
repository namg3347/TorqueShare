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
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;
    private final S3Service s3Service;
    private static final String GLOBAL_ID = "Global";

    @KafkaListener(topics = "torque-share-kafka", groupId = "analytics-service")
    public void consume(UploadCompletedEvent event) {
        log.info("Received upload completed event in analysis-service with event SLug: {}", event.getSlug());
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

        String decodedKey = URLDecoder.decode(event.getS3Key(), StandardCharsets.UTF_8);

        HeadObjectResponse head = s3Service.getHeadObjectResponse(decodedKey);

        long size =head.contentLength();
        String type = head.contentType();

        //ATOMIC(CONCURRENT) UPDATE
        Update update = new Update()
                //total upload update
                .inc("totalUploads", 1)
                .inc("totalSize",size);


        //image upload update
        if(type.startsWith("image/")) {
            update.inc("imageUploads", 1);
            update.inc("imageSize", size);
        }
        //video upload update
        if(type.startsWith("video/")) {
            update.inc("videoUploads", 1);
            update.inc("videoSize", size);
        }

        //file upload update
        else if(type.startsWith("application/")) {
            update.inc("fileUploads", 1);
            update.inc("fileSize", size);
        }

        mongoTemplate.upsert(
                new Query(where("_id").is(GLOBAL_ID)),
                update,
                UploadAnalytics.class
        );

        log.info("Analytics updated for slug={}", event.getSlug());
    }

}
