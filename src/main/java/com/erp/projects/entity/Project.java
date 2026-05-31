package com.erp.projects.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.projects.enums.ProjectStatus;
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

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "prj_projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNING;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal budget = BigDecimal.ZERO;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private int progress = 0;

    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        return (p.length == 1 ? p[0].substring(0, 1) : "" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase();
    }
}
