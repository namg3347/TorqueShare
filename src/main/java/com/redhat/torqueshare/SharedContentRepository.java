package com.redhat.torqueshare;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharedContentRepository extends MongoRepository<SharedContent,String> {
    //  finds the content vai mongodb queries
    Optional<SharedContent> findBySlug(String s);

    // checks if the slug even exists
    boolean existsBySlug(String s);
}
