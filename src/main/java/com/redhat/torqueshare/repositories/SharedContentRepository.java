package com.redhat.torqueshare.repositories;

import com.redhat.torqueshare.enums.SharedContentStatus;
import com.redhat.torqueshare.entities.SharedContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SharedContentRepository extends MongoRepository<SharedContent,String> {
    //  finds the content vai slug
    Optional<SharedContent> findBySlug(String s);

    // checks if the slug even exists
    boolean existsBySlug(String s);

    // finds the content via S3KEY
    Optional<SharedContent> findByS3Key(String s3Key);

    // gives list of all content with a certain status and created at a certain time
    List<SharedContent> findByStatusAndCreatedAtBefore(
            SharedContentStatus status,
            Instant time
    );

    // gives list of all content with a certain status
    List<SharedContent> findByStatus(SharedContentStatus status);
}
