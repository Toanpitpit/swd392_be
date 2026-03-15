package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.ModerationLog;
import fa.training.car_rental_management.repository.ModerationLogRepository;
import fa.training.car_rental_management.services.ModerationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ModerationLogServiceImpl implements ModerationLogService {

    @Autowired
    private ModerationLogRepository moderationLogRepository;

    @Override
    public ModerationLog createModerationLog(ModerationLog logs) {
        log.info("Creating moderation log by moderator: {}", logs.getModeratorId());
        return moderationLogRepository.save(logs);
    }

    @Override
    public Optional<ModerationLog> getModerationLogById(Integer id) {
        log.info("Fetching moderation log with id: {}", id);
        return moderationLogRepository.findById(id);
    }

    @Override
    public List<ModerationLog> getModerationLogsByModeratorId(Integer moderatorId) {
        log.info("Fetching moderation logs by moderator: {}", moderatorId);
        return moderationLogRepository.findByModeratorId(moderatorId);
    }

    @Override
    public List<ModerationLog> getModerationLogsByTargetEntityId(Integer targetEntityId) {
        log.info("Fetching moderation logs for target entity: {}", targetEntityId);
        return moderationLogRepository.findByTargetEntityId(targetEntityId);
    }

    @Override
    public List<ModerationLog> getAllModerationLogs() {
        log.info("Fetching all moderation logs");
        return moderationLogRepository.findAll();
    }

    @Override
    public ModerationLog updateModerationLog(ModerationLog logs) {
        log.info("Updating moderation log with id: {}", logs.getId());
        return moderationLogRepository.save(logs);
    }

    @Override
    public void deleteModerationLog(Integer id) {
        log.info("Deleting moderation log with id: {}", id);
        moderationLogRepository.deleteById(id);
    }
}

