package com.erp.settings.entity;

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
@Table(name = "company_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySettings extends BaseEntity {

    @Column(length = 150)
    @Builder.Default
    private String companyName = "Meridian ERP";

    @Column(length = 255)
    private String logo;

    @Column(length = 300)
    private String address;

    @Column(length = 40)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(length = 150)
    private String website;

    @Column(length = 10)
    @Builder.Default
    private String currency = "BDT";

    @Column(length = 60)
    @Builder.Default
    private String timezone = "Asia/Dhaka";

    @Column(length = 30)
    @Builder.Default
    private String fiscalYearStart = "July";
}
