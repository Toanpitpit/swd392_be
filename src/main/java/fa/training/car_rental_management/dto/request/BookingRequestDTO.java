package fa.training.car_rental_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {

    @NotNull(message = "Vehicle ID cannot be null")
    private Integer vehicleId;
    @NotNull(message = "Customer ID cannot be null")
    private Integer customerId;
    @NotNull(message = "Start time cannot be null")
    private LocalDateTime startTime;
    @NotNull(message = "End time cannot be null")
    private LocalDateTime endTime;
}

