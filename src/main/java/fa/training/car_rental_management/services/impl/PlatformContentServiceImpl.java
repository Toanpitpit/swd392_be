package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.PlatformContent;
import fa.training.car_rental_management.repository.PlatformContentRepository;
import fa.training.car_rental_management.services.PlatformContentService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlatformContentServiceImpl implements PlatformContentService {

    private final PlatformContentRepository platformContentRepository;

    public PlatformContentServiceImpl(PlatformContentRepository platformContentRepository) {
        this.platformContentRepository = platformContentRepository;
    }

    @Override
    public PlatformContent createPlatformContent(PlatformContent platformContent) {
        return platformContentRepository.save(platformContent);
    }

    @Override
    public Optional<PlatformContent> getPlatformContentById(Integer id) {
        return platformContentRepository.findById(id);
    }

    @Override
    public Optional<PlatformContent> getPlatformContentByKey(String key) {
        return platformContentRepository.findByKey(key);
    }

    @Override
    public PlatformContent updatePlatformContent(PlatformContent platformContent) {
        return platformContentRepository.save(platformContent);
    }

    @Override
    public void deletePlatformContent(Integer id) {
        platformContentRepository.deleteById(id);
    }
}

