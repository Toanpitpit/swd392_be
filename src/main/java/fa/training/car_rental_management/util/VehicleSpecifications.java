package fa.training.car_rental_management.util;

import fa.training.car_rental_management.entities.Address;
import fa.training.car_rental_management.entities.Availability;
import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.enums.AvailabilityType;
import fa.training.car_rental_management.enums.VehicleStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;


public class VehicleSpecifications {
    public static Specification<Vehicle> isAvailable(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            Predicate isActive = cb.equal(root.get("status"), VehicleStatus.ACTIVE);


            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<Availability> availabilityRoot = subquery.from(Availability.class);


            Predicate overlap = cb.and(
                    cb.lessThan(availabilityRoot.get("startDate"), endDate),
                    cb.greaterThan(availabilityRoot.get("endDate"), startDate)
            );


            Predicate isBusyStatus = cb.notEqual(availabilityRoot.get("type"), AvailabilityType.AVAILABLE);

            subquery.select(availabilityRoot.get("vehicle").get("id"))
                    .where(cb.and(overlap, isBusyStatus));

            return cb.and(isActive, cb.not(root.get("id").in(subquery)));
        };
    }

    /**
     * Lọc xe theo thành phố (Join qua bảng Address)
     */
    public static Specification<Vehicle> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isBlank()) {
                return null; // Trả về null để Specification ignore điều kiện này
            }

            Join<Vehicle, Address> addressJoin = root.join("address", JoinType.INNER);

            return cb.like(cb.lower(addressJoin.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    /**
     * Lọc theo hãng xe hoặc mẫu xe (Model)
     */
    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null || model.isBlank()) return null;
            return cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%");
        };
    }
}
