package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.UserSettings;
import fa.training.car_rental_management.repository.UserSettingsRepository;
import fa.training.car_rental_management.services.UserSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserSettingsServiceImpl implements UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Override
    public UserSettings createUserSettings(UserSettings settings) {
        log.info("Creating user settings for user: {}", settings.getUserId());
        return userSettingsRepository.save(settings);
    }

    @Override
    public Optional<UserSettings> getUserSettingsById(Integer id) {
        log.info("Fetching user settings with id: {}", id);
        return userSettingsRepository.findById(id);
    }

    @Override
    public Optional<UserSettings> getUserSettingsByUserId(Integer userId) {
        log.info("Fetching user settings for user: {}", userId);
        return userSettingsRepository.findByUserId(userId);
    }

    @Override
    public List<UserSettings> getAllUserSettings() {
        log.info("Fetching all user settings");
        return userSettingsRepository.findAll();
    }

    @Override
    public UserSettings updateUserSettings(UserSettings settings) {
        log.info("Updating user settings with id: {}", settings.getId());
        return userSettingsRepository.save(settings);
    }

    @Override
    public void deleteUserSettings(Integer id) {
        log.info("Deleting user settings with id: {}", id);
        userSettingsRepository.deleteById(id);
    }
}

