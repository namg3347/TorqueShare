package com.redhat.torqueshare.services;

import com.redhat.torqueshare.configs.S3Properties;
import com.redhat.torqueshare.exceptions.FileTypeNotAllowedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties properties;
    Set<String> allowed = Set.of("image/png", "image/jpeg", "application/pdf");

    // generates a upload url using s3presigner where user will upload
    public String generateUploadUrl(String key, String contentType) {
        if (!allowed.contains(contentType)) {
            log.error("Invalid content type: {}", contentType);
            throw new FileTypeNotAllowedException();
        }
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(properties.bucketName())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // Valid for 10 mins
                .putObjectRequest(objectRequest)
                .build();
        log.info("Pre-signer assigned to user for upload");
        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    //generates a download url where user can download
    public String generateDownloadUrl(String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(properties.bucketName())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // Valid for 15 mins
                .getObjectRequest(objectRequest)
                .build();
        log.info("Pre-signer assigned to user for download");
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public HeadObjectResponse getHeadObjectResponse(String key) {
        return s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(properties.bucketName())
                        .key(key)
                        .build());
    }

}
