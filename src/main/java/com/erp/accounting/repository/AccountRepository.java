package com.erp.accounting.repository;

import com.erp.accounting.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT a FROM Account a WHERE :q IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(a.code) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Account> search(@Param("q") String q, Pageable pageable);

    List<Account> findAllByOrderByTypeAscCodeAsc();
}
