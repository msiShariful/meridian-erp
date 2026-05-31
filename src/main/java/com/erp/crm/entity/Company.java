package com.erp.crm.entity;

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
@Table(name = "crm_companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 80)
    private String industry;

    @Column(length = 40)
    private String companySize;

    @Column(length = 150)
    private String website;

    @Column(length = 30)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(length = 300)
    private String address;

    @Column(length = 255)
    private String logo;

    @Column(length = 1000)
    private String description;

    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        return (p.length == 1 ? p[0].substring(0, 1) : "" + p[0].charAt(0) + p[1].charAt(0)).toUpperCase();
    }
}
