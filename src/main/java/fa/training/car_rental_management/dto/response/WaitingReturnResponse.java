package fa.training.car_rental_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WaitingReturnResponse {

    private Integer bookingId;
    private String licensePlate;
    private String vehicleName;
    private LocalDateTime endTime;
    private Double deposit; // thêm dòng này

}