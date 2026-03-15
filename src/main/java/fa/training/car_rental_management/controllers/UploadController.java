package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.services.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Upload Controller
 * Handles file and video uploads to AWS S3
 */
@Slf4j
@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Upload a single file
     * POST /api/uploads/file
     */
    @PostMapping("/file")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderPath", defaultValue = "files") String folderPath) {
        try {
            log.info("Uploading file: {}", file.getOriginalFilename());
            String fileUrl = uploadService.uploadFile(file, bucketName, folderPath);

            Map<String, String> data = new HashMap<>();
            data.put("fileUrl", fileUrl);
            data.put("fileName", file.getOriginalFilename());
            data.put("fileSize", String.valueOf(file.getSize()));

            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", data));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }

    /**
     * Upload multiple files
     * POST /api/uploads/files
     */
    @PostMapping("/files")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folderPath", defaultValue = "files") String folderPath) {
        try {
            log.info("Uploading {} files", files.size());
            List<String> fileUrls = uploadService.uploadMultipleFiles(files, bucketName, folderPath);

            Map<String, Object> data = new HashMap<>();
            data.put("fileUrls", fileUrls);
            data.put("totalFiles", files.size());

            return ResponseEntity.ok(ApiResponse.success("Files uploaded successfully", data));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading files", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload files: " + e.getMessage()));
        }
    }

    /**
     * Upload a single video
     * POST /api/uploads/video
     */
    @PostMapping("/video")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadVideo(
            @RequestParam("video") MultipartFile videoFile,
            @RequestParam(value = "folderPath", defaultValue = "videos") String folderPath) {
        try {
            log.info("Uploading video: {}", videoFile.getOriginalFilename());
            String videoUrl = uploadService.uploadVideo(videoFile, bucketName, folderPath);

            Map<String, String> data = new HashMap<>();
            data.put("videoUrl", videoUrl);
            data.put("videoName", videoFile.getOriginalFilename());
            data.put("videoSize", String.valueOf(videoFile.getSize()));

            return ResponseEntity.ok(ApiResponse.success("Video uploaded successfully", data));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading video", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload video: " + e.getMessage()));
        }
    }

    /**
     * Upload multiple videos
     * POST /api/uploads/videos
     */
    @PostMapping("/videos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadMultipleVideos(
            @RequestParam("videos") List<MultipartFile> videoFiles,
            @RequestParam(value = "folderPath", defaultValue = "videos") String folderPath) {
        try {
            log.info("Uploading {} videos", videoFiles.size());
            List<String> videoUrls = uploadService.uploadMultipleVideos(videoFiles, bucketName, folderPath);

            Map<String, Object> data = new HashMap<>();
            data.put("videoUrls", videoUrls);
            data.put("totalVideos", videoFiles.size());

            return ResponseEntity.ok(ApiResponse.success("Videos uploaded successfully", data));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading videos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload videos: " + e.getMessage()));
        }
    }

    /**
     * Delete a file
     * DELETE /api/uploads/delete?fileKey=path/to/file.txt
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @RequestParam("fileKey") String fileKey) {
        try {
            log.info("Deleting file: {}", fileKey);
            uploadService.deleteFile(bucketName, fileKey);
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete file: " + e.getMessage()));
        }
    }

    /**
     * Generate presigned URL for file access
     * GET /api/uploads/presigned-url?fileKey=path/to/file.txt&expirationMinutes=60
     */
    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> generatePresignedUrl(
            @RequestParam("fileKey") String fileKey,
            @RequestParam(value = "expirationMinutes", defaultValue = "60") int expirationMinutes) {
        try {
            log.info("Generating presigned URL for: {}", fileKey);
            String presignedUrl = uploadService.generatePresignedUrl(bucketName, fileKey, expirationMinutes);

            Map<String, String> data = new HashMap<>();
            data.put("presignedUrl", presignedUrl);
            data.put("expirationMinutes", String.valueOf(expirationMinutes));

            return ResponseEntity.ok(ApiResponse.success("Presigned URL generated successfully", data));
        } catch (Exception e) {
            log.error("Error generating presigned URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to generate presigned URL: " + e.getMessage()));
        }
    }

    /**
     * Check if file exists
     * GET /api/uploads/exists?fileKey=path/to/file.txt
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Map<String, Object>>> fileExists(
            @RequestParam("fileKey") String fileKey) {
        try {
            log.info("Checking file existence: {}", fileKey);
            boolean exists = uploadService.fileExists(bucketName, fileKey);

            Map<String, Object> data = new HashMap<>();
            data.put("fileKey", fileKey);
            data.put("exists", exists);

            String message = exists ? "File exists" : "File does not exist";
            return ResponseEntity.ok(ApiResponse.success(message, data));
        } catch (Exception e) {
            log.error("Error checking file existence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check file existence: " + e.getMessage()));
        }
    }
}

