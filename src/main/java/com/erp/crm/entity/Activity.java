package com.erp.crm.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.crm.enums.ActivityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A logged interaction (call, email, meeting, task or note) optionally tied to a
 * lead, contact or deal via a polymorphic reference.
 */
@Entity
@Table(name = "crm_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ActivityType type = ActivityType.CALL;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(length = 2000)
    private String notes;

    private LocalDateTime dueDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    @Column(length = 120)
    private String assignedTo;

    /** Related record type: LEAD, CONTACT or DEAL. */
    @Column(length = 20)
    private String relatedType;

    private UUID relatedId;

    @Column(length = 200)
    private String relatedLabel;
}
