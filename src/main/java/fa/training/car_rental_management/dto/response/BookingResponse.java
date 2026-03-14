package fa.training.car_rental_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Integer id;
    private Integer vehicleId;
    private Integer customerId;
    private String status;
    private String startTime;
    private String endTime;
}

