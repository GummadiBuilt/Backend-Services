package com.infra.gummadibuilt.notifications;

import com.infra.gummadibuilt.notifications.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDao extends JpaRepository<Notifications, Integer> {
}
