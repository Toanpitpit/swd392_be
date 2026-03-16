package fa.training.car_rental_management.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReturnCarRequest {

    private String comments;

    private String carStatus;

    private List<MultipartFile> files;

}