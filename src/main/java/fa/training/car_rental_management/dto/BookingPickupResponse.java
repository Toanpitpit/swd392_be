package fa.training.car_rental_management.dto;

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
public class BookingPickupResponse {
    private Integer bookingId;
    private String bookingStatus;
    private Integer vehicleId;
    private Integer customerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer paymentId;
    private BigDecimal paymentAmount;
    private String paymentStatus;
    private Integer inspectionId;
    private String inspectionType;
    private String carStatus;
    private String message;
}

