package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Users;
import fa.training.car_rental_management.repository.UserRepository;
import fa.training.car_rental_management.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Users createUser(Users user) {
        log.info("Creating new user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public Optional<Users> getUserById(Integer id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<Users> getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<Users> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public Users updateUser(Users user) {
        log.info("Updating user with id: {}", user.getId());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAllUsers() {
        log.info("Deleting all users");
        userRepository.deleteAll();
    }
}

