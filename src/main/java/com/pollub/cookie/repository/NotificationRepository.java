package com.pollub.cookie.repository;

import com.pollub.cookie.model.Notification;
import com.pollub.cookie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndReadFalse(User user);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.email = :username AND n.read = false")
    void markAllAsReadForUser(@Param("username") String username);
}
