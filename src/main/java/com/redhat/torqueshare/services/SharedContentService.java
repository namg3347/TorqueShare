package com.redhat.torqueshare.services;

import com.github.slugify.Slugify;
import com.redhat.torqueshare.exceptions.ContentNotFoundException;
import com.redhat.torqueshare.entities.SharedContent;
import com.redhat.torqueshare.repositories.SharedContentRepository;
import com.redhat.torqueshare.enums.SharedContentStatus;
import com.redhat.torqueshare.dto.UploadContentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SharedContentService {

    private final SharedContentRepository repository;

    // not thread safe to be worked on
    public String createSlug(String word) {
        Slugify slugify = Slugify.builder().build();
        String baseSlug = slugify.slugify(word);
        String finalSlug = baseSlug;
        int counter = 1;

        // We loop until a unique slug is found
        while (repository.existsBySlug(finalSlug)) {
            finalSlug = baseSlug + "-" + counter;
            counter++;
        }
        return finalSlug;
    }

    public SharedContent saveSharedContent(UploadContentRequest uploadContentRequest) {
        String slug = createSlug(uploadContentRequest.getOriginalWord());
        String key = "uploads/" + slug + "/" + UUID.randomUUID();
        SharedContent sharedContent =SharedContent.builder()
                .slug(slug)
                .contentType(uploadContentRequest.getContentType())
                .message(uploadContentRequest.getMessage())
                .s3Key(key)
                .fileSize(uploadContentRequest.getFileSize())
                .status(SharedContentStatus.PENDING)
                .createdAt(Instant.now())
                .expiryDate(Instant.now().plus(Duration.ofDays(1)))
                .build();
        return repository.save(sharedContent);
    }


    // mongodb query for content metadata
    @Cacheable(value = "shared_content", key = "#slug",
    unless = "#result == null || #result.expiryDate.isBefore(T(java.time.Instant).now())") //redis
    public SharedContent getSharedContent(String slug) {
        SharedContent content = repository.findBySlug(slug)
                .orElseThrow(ContentNotFoundException::new);

        if (content.getStatus() != SharedContentStatus.ACTIVE) {
            log.error("Shared content status not available");
            throw new ContentNotFoundException();
        }

        if (content.getExpiryDate().isBefore(Instant.now())) {
            log.error("Shared content expired");
            throw new ContentNotFoundException();
        }

        return content;
    }

    // marks active to successful upload for given s3 key
    public void markUploadComplete(String s3Key) {

        SharedContent content = repository.findByS3Key(s3Key)
                .orElseThrow(ContentNotFoundException::new);

        if (content.getStatus() == SharedContentStatus.ACTIVE) return;

        content.setStatus(SharedContentStatus.ACTIVE);
        content.setUploadedAt(Instant.now());

        repository.save(content);
    }

    //marks failure to unsuccessful upload for given s3 key
    public void markUploadFailure(String s3Key) {

        SharedContent content = repository.findByS3Key(s3Key)
                .orElseThrow(ContentNotFoundException::new);

        if (content.getStatus() == SharedContentStatus.FAILED) return;
        content.setStatus(SharedContentStatus.FAILED);
        repository.save(content);
    }

    // all the failed marked entries are marked expired as per schedule(half a day)
    @Scheduled(fixedDelay = 60000)
    public void cleanupFailed() {

        Instant threshold = Instant.now().minus(Duration.ofMinutes(15));

        repository.findByStatusAndCreatedAtBefore(
                SharedContentStatus.PENDING, threshold
        ).forEach(content -> {
            content.setStatus(SharedContentStatus.FAILED);
            repository.save(content);
        });
    }
}