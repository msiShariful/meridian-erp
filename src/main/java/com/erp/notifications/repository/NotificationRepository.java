package com.erp.notifications.repository;

import com.erp.notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT n FROM Notification n WHERE n.recipient IS NULL OR n.recipient = :email ORDER BY n.createdAt DESC")
    Page<Notification> forUser(@Param("email") String email, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.recipient IS NULL OR n.recipient = :email) AND n.readFlag = false")
    long countUnread(@Param("email") String email);

    @Query("SELECT n FROM Notification n WHERE (n.recipient IS NULL OR n.recipient = :email) AND n.readFlag = false")
    java.util.List<Notification> unread(@Param("email") String email);
}
