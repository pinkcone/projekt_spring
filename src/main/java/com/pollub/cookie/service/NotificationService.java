package com.pollub.cookie.service;

import com.pollub.cookie.model.User;

public interface NotificationService {

    void markAllAsReadForUser(String username);

    void createNotificationForAdmins(String message);

    void createNotificationForUser(User user, String message);
}
