package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.PlatformContent;

import java.util.Optional;

public interface PlatformContentService {
    PlatformContent createPlatformContent(PlatformContent platformContent);
    Optional<PlatformContent> getPlatformContentById(Integer id);
    Optional<PlatformContent> getPlatformContentByKey(String key);
    PlatformContent updatePlatformContent(PlatformContent platformContent);
    void deletePlatformContent(Integer id);
}

