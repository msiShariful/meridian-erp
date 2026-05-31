package com.erp.audit.repository;

import com.erp.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    @Query("""
            SELECT a FROM AuditLog a
            WHERE (:q IS NULL OR LOWER(a.actor) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(a.entityType) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:action IS NULL OR a.action = :action)
            ORDER BY a.createdAt DESC
            """)
    Page<AuditLog> search(@Param("q") String q, @Param("action") String action, Pageable pageable);
}
