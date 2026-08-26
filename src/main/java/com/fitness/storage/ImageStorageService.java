package com.fitness.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String endpoint;

    public ImageStorageService(
            S3Client s3Client,
            @Value("${storage.bucket-name}") String bucketName,
            @Value("${storage.endpoint}") String endpoint) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
    }

    // Automatically create the bucket when Spring Boot starts if it doesn't exist
    @PostConstruct
    public void initializeBucket() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            System.out.println("[MINIO] - Bucket '" + bucketName + "' already exists.");
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            System.out.println("[MINIO] - Created new bucket: " + bucketName);

            // Make the bucket public so mobile apps can download images without signing
            // URLs
            String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": "*",
                                "Action": "s3:GetObject",
                                "Resource": "arn:aws:s3:::%s/*"
                            }
                        ]
                    }
                    """.formatted(bucketName);

            s3Client.putBucketPolicy(PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(policy)
                    .build());
        }
    }

    public String uploadImage(MultipartFile file) {
        try {
            // Generate a random filename to prevent overwrites (e.g., photo-1234-abcd.jpg)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String newFileName = "photo-" + UUID.randomUUID() + extension;

            // Send the bytes to MinIO
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Return the public URL
            return endpoint + "/" + bucketName + "/" + newFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    public String uploadImageBytes(byte[] imageBytes, String extension, String contentType) {
        try {
            String newFileName = "map-" + UUID.randomUUID() + extension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(imageBytes));

            return endpoint + "/" + bucketName + "/" + newFileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload map bytes to S3", e);
        }
    }
}