package fa.training.car_rental_management.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * AWS S3 Configuration
 * Handles credentials from environment variables or application.properties
 */
@Slf4j
@Configuration
public class S3Config {

    @Value("${aws.s3.access-key:YOUR_AWS_ACCESS_KEY}")
    private String accessKey;

    @Value("${aws.s3.secret-key:YOUR_AWS_SECRET_KEY}")
    private String secretKey;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    /**
     * Create S3Client Bean
     * Validates credentials and logs configuration
     */
    @Bean
    public S3Client s3Client() {
        validateCredentials();
        log.info("Initializing S3Client with region: {}", region);
        
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * Create S3Presigner Bean
     * Used for generating presigned URLs
     */
    @Bean
    public S3Presigner s3Presigner() {
        validateCredentials();
        
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * Validate AWS credentials are properly configured
     */
    private void validateCredentials() {
        if (accessKey == null || accessKey.equals("YOUR_AWS_ACCESS_KEY")) {
            log.warn("⚠️  AWS_S3_ACCESS_KEY not configured. Set environment variable: AWS_S3_ACCESS_KEY");
            log.warn("   Or configure in application.properties: aws.s3.access-key");
        }
        
        if (secretKey == null || secretKey.equals("YOUR_AWS_SECRET_KEY")) {
            log.warn("⚠️  AWS_S3_SECRET_KEY not configured. Set environment variable: AWS_S3_SECRET_KEY");
            log.warn("   Or configure in application.properties: aws.s3.secret-key");
        }
        
        if (accessKey != null && secretKey != null && 
            !accessKey.equals("YOUR_AWS_ACCESS_KEY") && 
            !secretKey.equals("YOUR_AWS_SECRET_KEY")) {
            log.info("✓ AWS S3 credentials loaded successfully");
            log.info("✓ AWS Region: {}", region);
        }
    }
}