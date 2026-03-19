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

    private String vehicleName;   // 🔥 thêm
    private String status;

    private String startTime;
    private String endTime;

    private Double rentalFare;    // 🔥 tiền thuê
    private Double deposit;       // 🔥 tiền cọc
    private Double fine;          // 🔥 phí phạt
}
