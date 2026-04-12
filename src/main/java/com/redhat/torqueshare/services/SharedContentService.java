package com.redhat.torqueshare.services;

import com.github.slugify.Slugify;
import com.redhat.torqueshare.exceptions.ContentNotFoundException;
import com.redhat.torqueshare.SharedContent;
import com.redhat.torqueshare.SharedContentRepository;
import com.redhat.torqueshare.SharedContentStatus;
import com.redhat.torqueshare.dto.UploadContentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SharedContentService {

    private final SharedContentRepository repository;
    private final S3Service s3Service;

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
        SharedContent sharedContent =SharedContent.builder()
                .slug(slug)
                .contentType(uploadContentRequest.getContentType())
                .message(uploadContentRequest.getMessage())
                .s3Key(slug)
                .fileSize(uploadContentRequest.getFileSize())
                .status(SharedContentStatus.PENDING)
                .createdAt(Instant.now())
                .expiryDate(Instant.now().plus(Duration.ofDays(1)))
                .build();
        return repository.save(sharedContent);
    }

    public SharedContent getSharedContent(String slug) {
        return repository.findBySlug(slug).orElseThrow(ContentNotFoundException::new);
    }

}