package com.pollub.cookie.service.impl;

import com.pollub.cookie.model.Notification;
import com.pollub.cookie.model.Role;
import com.pollub.cookie.model.User;
import com.pollub.cookie.repository.NotificationRepository;
import com.pollub.cookie.repository.UserRepository;
import com.pollub.cookie.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void markAllAsReadForUser(String username) {
        notificationRepository.markAllAsReadForUser(username);
    }

    public void createNotificationForAdmins(String message) {
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for (User admin : admins) {
            Notification notification = new Notification();
            notification.setContent(message);
            notification.setRead(false);
            notification.setCreationDate(LocalDateTime.now());
            notification.setUser(admin);
            notificationRepository.save(notification);
        }
    }

    public void createNotificationForUser(User user, String message) {
        Notification notification = new Notification();
        notification.setContent(message);
        notification.setRead(false);
        notification.setCreationDate(LocalDateTime.now());
        notification.setUser(user);
        notificationRepository.save(notification);
    }
}
