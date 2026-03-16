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
@Table(name = "user_settings", schema = "carrentaldb")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "email_enabled", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean emailEnabled;

    @Column(name = "sms_enabled", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean smsEnabled;

    @Column(name = "push_enabled", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean pushEnabled;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;
}

