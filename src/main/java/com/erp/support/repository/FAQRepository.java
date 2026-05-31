package com.erp.support.repository;

import com.erp.support.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FAQRepository extends JpaRepository<FAQ, UUID> {
    List<FAQ> findAllByOrderByCategoryAscQuestionAsc();
}
