package fa.training.car_rental_management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnCarResponse {

    private Integer bookingId;

    private String bookingStatus;

    private Integer inspectionId;

    private Integer vehicleId;

    private String carStatus;

    private String message;

}