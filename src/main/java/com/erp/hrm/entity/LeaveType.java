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

@Entity
@Table(name = "hrm_leave_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType extends BaseEntity {

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private int daysPerYear = 0;
}
