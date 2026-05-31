package com.erp.crm.repository;

import com.erp.crm.entity.Deal;
import com.erp.crm.enums.DealStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DealRepository extends JpaRepository<Deal, UUID> {
    @Query("SELECT d FROM Deal d WHERE :q IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Deal> search(@Param("q") String q, Pageable pageable);

    List<Deal> findByStage(DealStage stage);

    @Query("SELECT COALESCE(SUM(d.value), 0) FROM Deal d WHERE d.stage = :stage")
    BigDecimal sumValueByStage(@Param("stage") DealStage stage);
}
