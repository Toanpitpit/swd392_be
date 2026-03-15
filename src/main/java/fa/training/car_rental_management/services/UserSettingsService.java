package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.UserSettings;

import java.util.List;
import java.util.Optional;

public interface UserSettingsService {
    UserSettings createUserSettings(UserSettings settings);
    Optional<UserSettings> getUserSettingsById(Integer id);
    Optional<UserSettings> getUserSettingsByUserId(Integer userId);
    List<UserSettings> getAllUserSettings();
    UserSettings updateUserSettings(UserSettings settings);
    void deleteUserSettings(Integer id);
}

