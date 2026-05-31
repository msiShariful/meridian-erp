package com.erp.notifications.service;

import com.erp.notifications.entity.Notification;
import com.erp.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class NotificationDataInitializer {

    private final NotificationRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(10)
    @Transactional
    public void seed() {
        if (repository.count() > 0) {
            return;
        }
        List<Notification> seed = List.of(
                Notification.builder().type("WARNING").title("Low stock alert")
                        .message("9 products have dropped below their reorder level.").link("/inventory/stock").build(),
                Notification.builder().type("SUCCESS").title("Leave approved")
                        .message("Nusrat Jahan approved a leave request.").link("/hrm/leave").build(),
                Notification.builder().type("DANGER").title("Invoice overdue")
                        .message("Invoice INV-0007 is past its due date.").link("/accounting/invoices").build(),
                Notification.builder().type("INFO").title("New support ticket")
                        .message("A new high-priority ticket was created.").link("/support/tickets").build(),
                Notification.builder().type("INFO").title("Task assigned")
                        .message("You were assigned a task on the Beximco project.").link("/projects/tasks").build()
        );
        repository.saveAll(seed);
        log.info("Seeded {} demo notifications.", seed.size());
    }
}
