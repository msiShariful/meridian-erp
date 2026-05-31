package com.erp.notifications.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * In-app notification. A null {@code recipient} means broadcast to all users.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    /** Recipient email, or null for a broadcast notification. */
    @Column(length = 150)
    private String recipient;

    @Column(nullable = false, length = 40)
    @Builder.Default
    private String type = "INFO";

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String message;

    @Column(length = 255)
    private String link;

    @Column(nullable = false)
    @Builder.Default
    private boolean readFlag = false;

    /** Tailwind colour token for the icon, derived from type. */
    public String getColor() {
        return switch (type) {
            case "SUCCESS" -> "success";
            case "WARNING" -> "warning";
            case "DANGER", "ALERT" -> "danger";
            default -> "brand";
        };
    }
}
