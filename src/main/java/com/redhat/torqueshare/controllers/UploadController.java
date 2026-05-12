package com.redhat.torqueshare.controllers;

import com.redhat.torqueshare.dto.UploadContentRequest;
import com.redhat.torqueshare.dto.UploadContentResponse;
import com.redhat.torqueshare.entities.SharedContent;
import com.redhat.torqueshare.services.S3Service;
import com.redhat.torqueshare.services.SharedContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UploadController {

    private final S3Service s3Service;
    private final SharedContentService sharedContentService;

    @PostMapping("/upload")
    public ResponseEntity<UploadContentResponse> uploadContent(@RequestBody UploadContentRequest request) {

        //save metadata in mongo
        SharedContent content = sharedContentService.saveSharedContent(request);

        //generate presigned url for uploading
        String uploadUrl = s3Service.generateUploadUrl(
                content.getS3Key(),
                content.getContentType());

        //return slug,upload-url,content type
        UploadContentResponse response = new  UploadContentResponse(
                content.getSlug(),uploadUrl,content.getExpiryDate());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
