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
@Table(name = "email_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSettings extends BaseEntity {

    @Column(length = 150)
    @Builder.Default
    private String smtpHost = "smtp.example.com";

    @Builder.Default
    private int smtpPort = 587;

    @Column(length = 150)
    private String username;

    @Column(length = 150)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean tlsEnabled = true;

    @Column(length = 150)
    @Builder.Default
    private String fromAddress = "no-reply@meridian-erp.com";
}
