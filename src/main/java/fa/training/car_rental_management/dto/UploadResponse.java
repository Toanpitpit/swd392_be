package fa.training.car_rental_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {

    @JsonProperty("fileKey")
    private String fileKey;

    @JsonProperty("presignedUrl")
    private String presignedUrl;

    @JsonProperty("originalFileName")
    private String originalFileName;

    @JsonProperty("fileSize")
    private Long fileSize;

    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("expirationInSeconds")
    private Integer expirationInSeconds;
}

