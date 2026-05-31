package com.erp.crm.repository;

import com.erp.crm.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    @Query("SELECT c FROM Contact c WHERE :q IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Contact> search(@Param("q") String q, Pageable pageable);

    List<Contact> findByCompanyId(UUID companyId);
}
