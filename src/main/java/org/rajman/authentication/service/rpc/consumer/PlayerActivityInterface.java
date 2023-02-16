package org.rajman.authentication.service.rpc.consumer;

import java.time.LocalDateTime;

public interface PlayerActivityInterface {
    void savePlayerActivity(Long playerId, String activitySlug, LocalDateTime actualDateTime);
}
