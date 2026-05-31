package com.erp.support.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.support.entity.Ticket;
import com.erp.support.entity.TicketMessage;
import com.erp.support.enums.TicketStatus;
import com.erp.support.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public Page<Ticket> search(String q, TicketStatus status, Pageable pageable) {
        return ticketRepository.search(blank(q), status, pageable);
    }

    @Transactional(readOnly = true)
    public Ticket get(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Ticket", id));
    }

    /** Generates the next ticket number, e.g. TKT-0001. */
    @Transactional(readOnly = true)
    public String nextTicketNumber() {
        return String.format("TKT-%04d", ticketRepository.count() + 1);
    }

    @Transactional
    public Ticket save(Ticket ticket) {
        if (ticket.getTicketNumber() == null || ticket.getTicketNumber().isBlank()) {
            ticket.setTicketNumber(nextTicketNumber());
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket addMessage(UUID ticketId, TicketMessage message) {
        Ticket ticket = get(ticketId);
        if (message.getSentAt() == null) {
            message.setSentAt(LocalDateTime.now());
        }
        ticket.addMessage(message);
        if (ticket.getFirstResponseAt() == null) {
            ticket.setFirstResponseAt(LocalDateTime.now());
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void updateStatus(UUID id, TicketStatus status) {
        Ticket ticket = get(id);
        ticket.setStatus(status);
        if ((status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) && ticket.getResolvedAt() == null) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        ticketRepository.save(ticket);
    }

    @Transactional
    public void delete(UUID id) {
        ticketRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
