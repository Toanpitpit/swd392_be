package fa.training.car_rental_management.entities;

import jakarta.persistence.*;
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
@Entity
@Table(name = "inspection_photos", schema = "carrentaldb")
public class InspectionPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "inspection_id", nullable = false)
    private Integer inspectionId;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", insertable = false, updatable = false)
    private Inspection inspection;
}

