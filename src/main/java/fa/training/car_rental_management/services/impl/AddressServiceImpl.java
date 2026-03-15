package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Address;
import fa.training.car_rental_management.repository.AddressRepository;
import fa.training.car_rental_management.services.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public Address createAddress(Address address) {
        log.info("Creating address for vehicle: {}", address.getVehicleId());
        return addressRepository.save(address);
    }

    @Override
    public Optional<Address> getAddressById(Integer id) {
        log.info("Fetching address with id: {}", id);
        return addressRepository.findById(id);
    }

    @Override
    public Optional<Address> getAddressByVehicleId(Integer vehicleId) {
        log.info("Fetching address for vehicle: {}", vehicleId);
        return addressRepository.findByVehicleId(vehicleId);
    }

    @Override
    public List<Address> getAllAddresses() {
        log.info("Fetching all addresses");
        return addressRepository.findAll();
    }

    @Override
    public Address updateAddress(Address address) {
        log.info("Updating address with id: {}", address.getId());
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Integer id) {
        log.info("Deleting address with id: {}", id);
        addressRepository.deleteById(id);
    }
}

