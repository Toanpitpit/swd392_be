package fa.training.car_rental_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    private Integer vehicleId;
    private Integer customerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

