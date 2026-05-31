package com.erp.support.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.support.enums.TicketPriority;
import com.erp.support.enums.TicketStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sup_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket extends BaseEntity {

    @Column(nullable = false, length = 20, unique = true)
    private String ticketNumber;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(length = 4000)
    private String description;

    @Column(length = 150)
    private String customerName;

    @Column(length = 150)
    private String customerEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketPriority priority = TicketPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

    @Column(length = 120)
    private String assignedTo;

    @Column(nullable = false)
    @Builder.Default
    private int slaResponseHours = 24;

    @Column(nullable = false)
    @Builder.Default
    private int slaResolutionHours = 48;

    private LocalDateTime firstResponseAt;

    private LocalDateTime resolvedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sentAt ASC")
    @Builder.Default
    private List<TicketMessage> messages = new ArrayList<>();

    /** True when an unresolved ticket has blown past its SLA resolution window. */
    public boolean slaBreached() {
        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            return false;
        }
        return getCreatedAt() != null
                && getCreatedAt().plusHours(slaResolutionHours).isBefore(LocalDateTime.now());
    }

    public void addMessage(TicketMessage message) {
        message.setTicket(this);
        messages.add(message);
    }
}
