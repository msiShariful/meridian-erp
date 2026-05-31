package com.erp.audit.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String actor;

    @Column(nullable = false, length = 20)
    private String action;

    @Column(length = 80)
    private String entityType;

    @Column(length = 500)
    private String details;

    @Column(length = 60)
    private String ipAddress;

    public String getActionColor() {
        return switch (action) {
            case "CREATE" -> "success";
            case "DELETE" -> "danger";
            case "UPDATE" -> "warning";
            default -> "info";
        };
    }
}
