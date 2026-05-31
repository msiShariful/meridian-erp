package com.erp.accounting.repository;

import com.erp.accounting.entity.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {

    @Query("SELECT j FROM JournalEntry j WHERE :q IS NULL OR LOWER(j.reference) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<JournalEntry> search(@Param("q") String q, Pageable pageable);
}
