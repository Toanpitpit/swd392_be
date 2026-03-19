package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.dto.response.WaitingReturnResponse;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByVehicleId(Integer vehicleId);
    List<Booking> findByCustomerId(Integer customerId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByVehicleIdAndCustomerId(Integer vehicleId, Integer customerId);
    @Query("""
SELECT new fa.training.car_rental_management.dto.response.WaitingReturnResponse(
    b.id,
    v.licensePlate,
    CONCAT(v.make,' ',v.model),
    b.endTime,
    (
        SELECT COALESCE(SUM(p.amount), 0.0)
        FROM Payment p
        WHERE p.booking.id = b.id
        AND p.type = fa.training.car_rental_management.enums.PaymentType.SECURITY_DEPOSIT
    )
)
FROM Booking b
JOIN b.vehicle v
WHERE b.status = 'UNDER_INSPECTION'
AND v.owner.id = :ownerId
""")
    List<WaitingReturnResponse> findWaitingReturnConfirm(Integer ownerId);
    @Query(value = """
    SELECT 
        b.id,
        b.vehicle_id,
        b.customer_id,
        b.status,
        b.start_time,
        b.end_time,

        v.make,
        v.model,

        SUM(CASE WHEN p.type = 'RENTAL_FARE' THEN p.amount ELSE 0 END) AS rental_fare,
        SUM(CASE WHEN p.type = 'SECURITY_DEPOSIT' THEN p.amount ELSE 0 END) AS deposit,
        SUM(CASE WHEN p.type = 'FINE' AND p.status = 'PENDING' THEN p.amount ELSE 0 END) AS fine

    FROM Bookings b
    LEFT JOIN Vehicles v ON v.id = b.vehicle_id
    LEFT JOIN Payments p ON p.booking_id = b.id

    WHERE b.customer_id = :customerId

    GROUP BY 
        b.id, b.vehicle_id, b.customer_id, b.status, 
        b.start_time, b.end_time,
        v.make, v.model

    ORDER BY b.created_at DESC
""", nativeQuery = true)
    List<Object[]> findBookingDetailsByCustomer(@Param("customerId") int customerId);
}

