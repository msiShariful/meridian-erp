package com.erp.support.repository;

import com.erp.support.entity.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, UUID> {
}
