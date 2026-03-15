package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    Address createAddress(Address address);
    Optional<Address> getAddressById(Integer id);
    Optional<Address> getAddressByVehicleId(Integer vehicleId);
    List<Address> getAllAddresses();
    Address updateAddress(Address address);
    void deleteAddress(Integer id);
}

