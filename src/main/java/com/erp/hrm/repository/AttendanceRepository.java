package com.erp.hrm.repository;

import com.erp.hrm.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    @Query("SELECT a FROM Attendance a WHERE :date IS NULL OR a.date = :date")
    Page<Attendance> findByDate(@Param("date") LocalDate date, Pageable pageable);
}
