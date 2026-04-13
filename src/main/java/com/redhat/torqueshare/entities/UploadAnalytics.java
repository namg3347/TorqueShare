package com.redhat.torqueshare.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "upload_analytics")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadAnalytics {
    @Id
    private String id; // global

    private long totalUploads; // all uploads to s3
    private long totalSize; // sum of size of all uploads

    private long imageUploads; // all image uploads to s3
    private long imageSize; // sum of size of all image uploads

    private long fileUploaded; // all file uploads to s3
    private long fileSize; // sum of size of all file uploads

}
