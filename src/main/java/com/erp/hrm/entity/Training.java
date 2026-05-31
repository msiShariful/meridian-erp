package com.erp.hrm.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hrm_trainings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Training extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 120)
    private String trainer;

    @Column(length = 60)
    private String type;

    private LocalDate startDate;

    @Column(nullable = false)
    @Builder.Default
    private int durationDays = 1;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(length = 40)
    @Builder.Default
    private String status = "PLANNED";
}
