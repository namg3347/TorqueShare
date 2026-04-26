package com.redhat.torqueshare.controllers;

import com.redhat.torqueshare.entities.SharedContent;
import com.redhat.torqueshare.services.S3Service;
import com.redhat.torqueshare.services.SharedContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class DownloadController {

    private final S3Service s3Service;
    private final SharedContentService  sharedContentService;

    @GetMapping("/download/{slug}")
    public ResponseEntity<String> downloadContent(@PathVariable String slug) {
        SharedContent content =  sharedContentService.getSharedContent(slug);
        String downloadUrl =  s3Service.generateDownloadUrl(content.getS3Key());
        return new ResponseEntity<>(downloadUrl, HttpStatus.OK);
    }
}
