package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Wishlist;
import fa.training.car_rental_management.repository.WishlistRepository;
import fa.training.car_rental_management.services.WishlistService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public Wishlist createWishlist(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    @Override
    public Optional<Wishlist> getWishlistById(Integer id) {
        return wishlistRepository.findById(id);
    }

    @Override
    public List<Wishlist> getWishlistsByCustomerId(Integer customerId) {
        return wishlistRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Wishlist> getWishlistsByVehicleId(Integer vehicleId) {
        return wishlistRepository.findByVehicleId(vehicleId);
    }

    @Override
    public Wishlist updateWishlist(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    @Override
    public void deleteWishlist(Integer id) {
        wishlistRepository.deleteById(id);
    }
}

