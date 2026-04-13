package com.redhat.torqueshare.entities;

import com.redhat.torqueshare.SharedContentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Document(collection = "content")
public class SharedContent {

    @Id
    private String slug;    // unique slug(word) to be generated from given word

    private String originalWord;    // word provided by user

    private String contentType;     //helps in type queries

    // S3 specific info
    private String s3Key;          // The path/name of the file inside my S3 bucket
    private String message;        // Optional text provided by the user

    @Indexed(expireAfter = "0")
    private Instant expiryDate;

    @Builder.Default
    private SharedContentStatus status = SharedContentStatus.PENDING; // PENDING, ACTIVE, or EXPIRED

    private Instant createdAt;     // For auditing and logging tracking
    private long fileSize;         // to show the user about the file size

    private Instant uploadedAt;      //null until file is uploaded in s3




}
