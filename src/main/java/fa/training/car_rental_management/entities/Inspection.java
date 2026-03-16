package fa.training.car_rental_management.entities;

import fa.training.car_rental_management.enums.CarStatus;
import fa.training.car_rental_management.enums.InspectionType;
import jakarta.persistence.*;
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
@Entity
@Table(name = "inspections", schema = "carrentaldb")
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @Column(name = "inspector_id", nullable = false)
    private Integer inspectorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
    private InspectionType type;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_status", length = 50)
    private CarStatus carStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", insertable = false, updatable = false)
    private Users inspector;
}

