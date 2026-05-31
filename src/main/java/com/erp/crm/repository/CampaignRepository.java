package com.erp.crm.repository;

import com.erp.crm.entity.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    @Query("SELECT c FROM Campaign c WHERE :q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Campaign> search(@Param("q") String q, Pageable pageable);
}
