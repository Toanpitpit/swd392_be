package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.dto.UploadResponse;
import fa.training.car_rental_management.services.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.max-file-size:52428800}")
    private long maxFileSize;


    @Value("${aws.s3.allowed-file-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,mp4}")

    private String allowedFileTypes;

    @Value("${aws.s3.allowed-video-types:mp4,avi,mov,mkv,flv,wmv,webm}")
    private String allowedVideoTypes;

    @Value("${aws.s3.max-video-size:5368709120}")
    private long maxVideoSize; // 5GB default

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public UploadServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public UploadResponse uploadFile(MultipartFile file, String bucketName, String folderPath) throws IOException {
        validateFile(file);
        return performUploadWithPresignedUrl(file, bucketName, folderPath);
    }

    @Override
    public List<UploadResponse> uploadMultipleFiles(List<MultipartFile> files, String bucketName, String folderPath) throws IOException {
        List<UploadResponse> uploadResponses = new ArrayList<>();
        for (MultipartFile file : files) {
            validateFile(file);
            UploadResponse response = performUploadWithPresignedUrl(file, bucketName, folderPath);
            uploadResponses.add(response);
        }
        return uploadResponses;
    }

    @Override
    public UploadResponse uploadVideo(MultipartFile videoFile, String bucketName, String folderPath) throws IOException {
        validateVideo(videoFile);
        return performUploadWithPresignedUrl(videoFile, bucketName, folderPath);
    }

    @Override
    public List<UploadResponse> uploadMultipleVideos(List<MultipartFile> videoFiles, String bucketName, String folderPath) throws IOException {
        List<UploadResponse> uploadResponses = new ArrayList<>();
        for (MultipartFile videoFile : videoFiles) {
            validateVideo(videoFile);
            UploadResponse response = performUploadWithPresignedUrl(videoFile, bucketName, folderPath);
            uploadResponses.add(response);
        }
        return uploadResponses;
    }

    @Override
    public void deleteFile(String bucketName, String fileKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}/{}", bucketName, fileKey);
        } catch (Exception e) {
            log.error("Error deleting file: {}/{}", bucketName, fileKey, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public String generatePresignedUrl(String bucketName, String fileKey, int expirationMinutes) {
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .getObjectRequest(builder -> builder.bucket(bucketName).key(fileKey).build())
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL for: {}/{}", bucketName, fileKey, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    @Override
    public boolean fileExists(String bucketName, String fileKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            return response.contentLength() > 0;
        } catch (NoSuchKeyException e) {
            log.debug("File does not exist: {}/{}", bucketName, fileKey);
            return false;
        } catch (Exception e) {
            log.error("Error checking file existence: {}/{}", bucketName, fileKey, e);
            throw new RuntimeException("Failed to check file existence", e);
        }
    }



    private UploadResponse performUploadWithPresignedUrl(
            MultipartFile file,
            String bucketName,
            String folderPath) throws IOException {
        
        try {
            log.debug("Starting upload for file: {} to bucket: {} with folder: {}", 
                    file.getOriginalFilename(), bucketName, folderPath);
            
            String fileName = generateFileName(file.getOriginalFilename());
            String fileKey = folderPath.isEmpty() ? fileName : folderPath + "/" + fileName;

            log.debug("Uploading with SSE-S3 encryption to: s3://{}/{}", bucketName, fileKey);
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                    file.getInputStream(), 
                    file.getSize()));

            log.info("File uploaded successfully to S3: {}", fileKey);

            int expirationMinutes = 15;
            String presignedUrl = generatePresignedUrl(bucketName, fileKey, expirationMinutes);
            log.info("Presigned URL generated (valid for {} minutes): {}", expirationMinutes, fileKey);

            return UploadResponse.builder()
                    .fileKey(fileKey)
                    .presignedUrl(presignedUrl)
                    .originalFileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .expirationInSeconds(expirationMinutes * 60)
                    .build();
            
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error("AWS S3 Error: {}", e.awsErrorDetails().errorMessage());
            log.error("Error Code: {} (HTTP {})", e.awsErrorDetails().errorCode(), e.statusCode());
            
            if (e.statusCode() == 403) {
                throw new IOException("S3 Access Denied - Check AWS credentials and permissions", e);
            } else if (e.statusCode() == 404) {
                throw new IOException("S3 Bucket not found: " + bucketName, e);
            } else {
                throw new IOException("AWS S3 Error: " + e.awsErrorDetails().errorMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    
    private String buildS3Url(String bucket, String fileKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileKey);
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size: " + maxFileSize);
        }

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed: " + fileExtension);
        }
    }

    private void validateVideo(MultipartFile videoFile) throws IOException {
        if (videoFile.isEmpty()) {
            throw new IllegalArgumentException("Video file is empty");
        }

        if (videoFile.getSize() > maxVideoSize) {
            throw new IllegalArgumentException("Video file size exceeds maximum allowed size: " + maxVideoSize);
        }

        String videoExtension = FilenameUtils.getExtension(videoFile.getOriginalFilename()).toLowerCase();
        if (!isAllowedVideoType(videoExtension)) {
            throw new IllegalArgumentException("Video type not allowed: " + videoExtension);
        }
    }

    private boolean isAllowedFileType(String extension) {
        String[] allowedTypes = allowedFileTypes.split(",");
        for (String type : allowedTypes) {
            if (type.trim().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedVideoType(String extension) {
        String[] allowedTypes = allowedVideoTypes.split(",");
        for (String type : allowedTypes) {
            if (type.trim().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private String generateFileName(String originalFileName) {
        String fileExtension = FilenameUtils.getExtension(originalFileName);
        return UUID.randomUUID() + "." + fileExtension;
    }
}

