package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Users;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Users createUser(Users user);
    Optional<Users> getUserById(Integer id);
    Optional<Users> getUserByEmail(String email);
    List<Users> getAllUsers();
    Users updateUser(Users user);
    void deleteUser(Integer id);
    void deleteAllUsers();
}

