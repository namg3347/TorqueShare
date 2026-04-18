package com.redhat.torqueshare.services;

import com.redhat.torqueshare.entities.SharedContent;
import com.redhat.torqueshare.repositories.SharedContentRepository;
import com.redhat.torqueshare.enums.SharedContentStatus;
import com.redhat.torqueshare.events.UploadCompletedEvent;
import com.redhat.torqueshare.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    private final S3Service s3Service;
    private final SharedContentRepository repository;
    private final SharedContentService  sharedContentService;
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/png", "image/jpeg", "application/pdf","application/zip","application/octet-stream");

    @KafkaListener(topics = "torque-share-kafka", groupId = "validation-service")
    public void validate(UploadCompletedEvent event) {
        log.info("Received upload completed event in validation-service");
        try {

            process(event);

        } catch (Exception e) {
            log.error("Failed to process message: {}", event, e);
        }
    }

    //validation process
    private void process(UploadCompletedEvent event) {

        String decodedKey = URLDecoder.decode(event.getS3Key(), StandardCharsets.UTF_8);


        SharedContent content = repository.findBySlug(event.getSlug())
                .orElseThrow(ContentNotFoundException::new);

        //key validation
        if (!decodedKey.equals(content.getS3Key())) {
            throw new ContentKeyNotMatchingException();
        }

        // Idempotency check
        if (content.getStatus() != SharedContentStatus.PENDING) {
            log.info("Skipping already processed: {}", event.getSlug());
            return;
        }

        try {
            HeadObjectResponse head = s3Service.getHeadObjectResponse(decodedKey);

            validateSize(head, content);
            validateContentType(head, content);

            // SUCCESS
            sharedContentService.markUploadComplete(content.getS3Key());
            log.info("Validation successful: {}", content.getSlug());

        } catch (ContentSizeNotMatchingException | ContentTypeNotMatchingException e) {

            // FAILURE
            sharedContentService.markUploadFailure(content.getS3Key());

            log.error("Validation failed for {}", content.getSlug(), e);
        } catch (Exception e) {
            log.error("Failed to process message: {}", event, e);
        }
    }


    // VALIDATION LOGIC
    private void validateSize(HeadObjectResponse head, SharedContent content) {

        long actual = head.contentLength();
        long expected = content.getFileSize();

        if (actual != expected) {
            log.error("Size mismatch: expected={}, actual={}", expected, actual);
            throw new ContentSizeNotMatchingException();
        }
    }

    private void validateContentType(HeadObjectResponse head, SharedContent content) {

        String actual = head.contentType();
        String expected = content.getContentType();

        if (!ALLOWED_TYPES.contains(actual)) {
            log.error("Invalid content type: {}", actual);
            throw new FileTypeNotAllowedException();
        }

        if (!actual.equals(expected)) {
            log.error("Content type mismatch: expected={}, actual={}", expected, actual);
            throw new ContentTypeNotMatchingException();
        }
    }


}