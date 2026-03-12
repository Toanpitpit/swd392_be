package fa.training.car_rental_management.dto;

import fa.training.car_rental_management.enums.CarStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPickupRequest {
    private Integer vehicleId;
    private Integer customerId;
    private Integer inspectorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal securityDepositAmount;
    private CarStatus carStatus;
    private String inspectionComments;
}

