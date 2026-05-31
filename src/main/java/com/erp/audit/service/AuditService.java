package com.erp.audit.service;

import com.erp.audit.entity.AuditLog;
import com.erp.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    /** Records an audit entry in its own transaction so it never affects the audited operation. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String actor, String action, String entityType, String details, String ip) {
        repository.save(AuditLog.builder()
                .actor(actor).action(action).entityType(entityType).details(details).ipAddress(ip).build());
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> search(String q, String action, Pageable pageable) {
        return repository.search(blank(q), blank(action), pageable);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
