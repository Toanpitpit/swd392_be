package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Wishlist;

import java.util.List;
import java.util.Optional;

public interface WishlistService {
    Wishlist createWishlist(Wishlist wishlist);
    Optional<Wishlist> getWishlistById(Integer id);
    List<Wishlist> getWishlistsByCustomerId(Integer customerId);
    List<Wishlist> getWishlistsByVehicleId(Integer vehicleId);
    Wishlist updateWishlist(Wishlist wishlist);
    void deleteWishlist(Integer id);
}

