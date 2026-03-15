package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.users;

import java.util.List;
import java.util.Optional;

public interface UserService {
    users createUser(users user);
    Optional<users> getUserById(Integer id);
    Optional<users> getUserByEmail(String email);
    List<users> getAllUsers();
    users updateUser(users user);
    void deleteUser(Integer id);
    void deleteAllUsers();
}

