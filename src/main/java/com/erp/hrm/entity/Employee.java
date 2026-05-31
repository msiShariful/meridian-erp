package com.erp.hrm.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.hrm.enums.EmployeeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hrm_employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Column(length = 20)
    private String employeeId;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(length = 80)
    private String lastName;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private Designation designation;

    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Column(length = 20)
    private String gender;

    private LocalDate dateOfBirth;

    @Column(length = 255)
    private String address;

    @Column(length = 60)
    private String bankAccount;

    @Column(length = 60)
    private String emergencyContact;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal basicSalary = BigDecimal.ZERO;

    @Column(length = 255)
    private String photo;

    public String getFullName() {
        return (firstName == null ? "" : firstName) + (lastName == null || lastName.isBlank() ? "" : " " + lastName);
    }

    public String getInitials() {
        String f = (firstName != null && !firstName.isBlank()) ? firstName.substring(0, 1) : "";
        String l = (lastName != null && !lastName.isBlank()) ? lastName.substring(0, 1) : "";
        String r = (f + l).toUpperCase();
        return r.isBlank() ? "?" : r;
    }
}
