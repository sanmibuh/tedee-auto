package org.sanmibuh.tedee.automation.notification.application;

import org.sanmibuh.tedee.automation.notification.domain.NotificationPort;
import org.springframework.stereotype.Service;

@Service
public class SendNotificationUseCase {

	private final NotificationPort notificationPort;

	public SendNotificationUseCase(NotificationPort notificationPort) {
		this.notificationPort = notificationPort;
	}

	public void execute(String message) {
		this.notificationPort.send(message);
	}
}
