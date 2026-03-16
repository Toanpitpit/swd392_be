package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Wishlist;
import fa.training.car_rental_management.services.WishlistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/wishlists")
@CrossOrigin(origins = "*")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<Wishlist>> createWishlist(@RequestBody Wishlist wishlist) {
        try {
            Wishlist createdWishlist = wishlistService.createWishlist(wishlist);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Wishlist created successfully", createdWishlist));
        } catch (Exception e) {
            log.error("Error creating wishlist", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create wishlist: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Wishlist>> getWishlistById(@PathVariable Integer id) {
        try {
            Optional<Wishlist> wishlist = wishlistService.getWishlistById(id);
            return wishlist.map(w -> ResponseEntity.ok(ApiResponse.success("Wishlist retrieved", w)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Wishlist not found")));
        } catch (Exception e) {
            log.error("Error retrieving wishlist", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving wishlist: " + e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Wishlist>>> getWishlistsByCustomerId(@PathVariable Integer customerId) {
        try {
            List<Wishlist> wishlists = wishlistService.getWishlistsByCustomerId(customerId);
            return ResponseEntity.ok(ApiResponse.success("Wishlists retrieved", wishlists));
        } catch (Exception e) {
            log.error("Error retrieving wishlists", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving wishlists: " + e.getMessage()));
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<Wishlist>>> getWishlistsByVehicleId(@PathVariable Integer vehicleId) {
        try {
            List<Wishlist> wishlists = wishlistService.getWishlistsByVehicleId(vehicleId);
            return ResponseEntity.ok(ApiResponse.success("Wishlists retrieved", wishlists));
        } catch (Exception e) {
            log.error("Error retrieving wishlists", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving wishlists: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Wishlist>> updateWishlist(@PathVariable Integer id, @RequestBody Wishlist wishlist) {
        try {
            wishlist.setId(id);
            Wishlist updatedWishlist = wishlistService.updateWishlist(wishlist);
            return ResponseEntity.ok(ApiResponse.success("Wishlist updated successfully", updatedWishlist));
        } catch (Exception e) {
            log.error("Error updating wishlist", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update wishlist: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWishlist(@PathVariable Integer id) {
        try {
            wishlistService.deleteWishlist(id);
            return ResponseEntity.ok(ApiResponse.success("Wishlist deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting wishlist", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete wishlist: " + e.getMessage()));
        }
    }
}

