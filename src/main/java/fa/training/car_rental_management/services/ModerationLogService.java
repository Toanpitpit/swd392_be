package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.ModerationLog;

import java.util.List;
import java.util.Optional;

public interface ModerationLogService {
    ModerationLog createModerationLog(ModerationLog log);
    Optional<ModerationLog> getModerationLogById(Integer id);
    List<ModerationLog> getModerationLogsByModeratorId(Integer moderatorId);
    List<ModerationLog> getModerationLogsByTargetEntityId(Integer targetEntityId);
    List<ModerationLog> getAllModerationLogs();
    ModerationLog updateModerationLog(ModerationLog log);
    void deleteModerationLog(Integer id);
}

