package com.erp.support.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.support.enums.TicketPriority;
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

@Entity
@Table(name = "sup_slas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SLA extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketPriority priority = TicketPriority.MEDIUM;

    @Column(nullable = false)
    @Builder.Default
    private int responseHours = 24;

    @Column(nullable = false)
    @Builder.Default
    private int resolutionHours = 48;
}
