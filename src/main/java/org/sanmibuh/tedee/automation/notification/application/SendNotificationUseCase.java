package org.sanmibuh.tedee.automation.notification.application;

import org.sanmibuh.tedee.automation.notification.domain.NotificationPort;
import org.springframework.stereotype.Service;

/**
 * Use case: send an alert notification.
 */
@Service
public class SendNotificationUseCase {

    private final NotificationPort notificationPort;

    public SendNotificationUseCase(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    public void execute(String message) {
        notificationPort.send(message);
    }
}
