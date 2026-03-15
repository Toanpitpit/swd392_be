package fa.training.car_rental_management.entities;

import fa.training.car_rental_management.enums.ModerationAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "moderation_logs", schema = "carrentaldb")
public class ModerationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "moderator_id", nullable = false)
    private Integer moderatorId;

    @Column(name = "target_entity_id", nullable = false)
    private Integer targetEntityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_taken", length = 100)
    private ModerationAction actionTaken;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id", insertable = false, updatable = false)
    private users moderator;
}

