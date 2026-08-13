package org.sanmibuh.tedee.automation.notification.domain;

/**
 * Output port: send a notification message.
 */
public interface NotificationPort {

    void send(String message);
}
