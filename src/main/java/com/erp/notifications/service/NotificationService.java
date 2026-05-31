package com.erp.notifications.service;

import com.erp.notifications.entity.Notification;
import com.erp.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    @Transactional(readOnly = true)
    public Page<Notification> forUser(String email, Pageable pageable) {
        return repository.forUser(email, pageable);
    }

    @Transactional(readOnly = true)
    public long unreadCount(String email) {
        return email == null ? 0 : repository.countUnread(email);
    }

    @Transactional
    public void markAllRead(String email) {
        repository.unread(email).forEach(n -> {
            n.setReadFlag(true);
            repository.save(n);
        });
    }

    @Transactional
    public void markRead(UUID id) {
        repository.findById(id).ifPresent(n -> {
            n.setReadFlag(true);
            repository.save(n);
        });
    }

    @Transactional
    public Notification notify(String recipient, String type, String title, String message, String link) {
        return repository.save(Notification.builder()
                .recipient(recipient).type(type).title(title).message(message).link(link).build());
    }
}
