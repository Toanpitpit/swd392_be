package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.UploadResponse;
import fa.training.car_rental_management.services.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @PostMapping("/file")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderPath", defaultValue = "files") String folderPath) {
        try {
            log.info("Upload file: {}", file.getOriginalFilename());
            UploadResponse response = uploadService.uploadFile(file, bucketName, folderPath);
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }

    @PostMapping("/files")
    public ResponseEntity<ApiResponse<List<UploadResponse>>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folderPath", defaultValue = "files") String folderPath) {
        try {
            log.info("Upload {} files", files.size());
            List<UploadResponse> responses = uploadService.uploadMultipleFiles(files, bucketName, folderPath);
            return ResponseEntity.ok(ApiResponse.success("Files uploaded successfully", responses));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading files", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload files: " + e.getMessage()));
        }
    }

    @PostMapping("/video")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadVideo(
            @RequestParam("video") MultipartFile videoFile,
            @RequestParam(value = "folderPath", defaultValue = "videos") String folderPath) {
        try {
            log.info("Upload video: {}", videoFile.getOriginalFilename());
            UploadResponse response = uploadService.uploadVideo(videoFile, bucketName, folderPath);
            return ResponseEntity.ok(ApiResponse.success("Video uploaded successfully", response));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading video", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload video: " + e.getMessage()));
        }
    }

    @PostMapping("/videos")
    public ResponseEntity<ApiResponse<List<UploadResponse>>> uploadMultipleVideos(
            @RequestParam("videos") List<MultipartFile> videoFiles,
            @RequestParam(value = "folderPath", defaultValue = "videos") String folderPath) {
        try {
            log.info("Upload {} videos", videoFiles.size());
            List<UploadResponse> responses = uploadService.uploadMultipleVideos(videoFiles, bucketName, folderPath);
            return ResponseEntity.ok(ApiResponse.success("Videos uploaded successfully", responses));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading videos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload videos: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam("fileKey") String fileKey) {
        try {
            log.info("Delete file: {}", fileKey);
            uploadService.deleteFile(bucketName, fileKey);
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete file: " + e.getMessage()));
        }
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<?>> getPresignedUrl(
            @RequestParam("fileKey") String fileKey,
            @RequestParam(value = "expirationMinutes", defaultValue = "60") int expirationMinutes) {
        try {
            log.info("Generate presigned URL for: {} (expires in {} minutes)", fileKey, expirationMinutes);
            
            if (fileKey == null || fileKey.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("fileKey is required"));
            }

            if (expirationMinutes < 1 || expirationMinutes > 1440) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("expirationMinutes must be between 1 and 1440"));
            }

            String presignedUrl = uploadService.generatePresignedUrl(bucketName, fileKey, expirationMinutes);
            return ResponseEntity.ok(ApiResponse.success("Presigned URL generated", 
                    Map.of(
                            "presignedUrl", presignedUrl,
                            "expirationInSeconds", expirationMinutes * 60,
                            "fileKey", fileKey
                    )));
        } catch (Exception e) {
            log.error("Error generating presigned URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to generate presigned URL: " + e.getMessage()));
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<?>> fileExists(@RequestParam("fileKey") String fileKey) {
        try {
            log.info("Check file exists: {}", fileKey);
            
            if (fileKey == null || fileKey.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("fileKey is required"));
            }

            boolean exists = uploadService.fileExists(bucketName, fileKey);
            return ResponseEntity.ok(ApiResponse.success("File check completed",
                    Map.of(
                            "fileKey", fileKey,
                            "exists", exists
                    )));
        } catch (Exception e) {
            log.error("Error checking file existence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check file: " + e.getMessage()));
        }
    }
}

