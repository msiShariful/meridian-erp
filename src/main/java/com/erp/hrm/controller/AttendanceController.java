package com.erp.hrm.controller;

import com.erp.hrm.entity.Attendance;
import com.erp.hrm.enums.AttendanceStatus;
import com.erp.hrm.service.AttendanceService;
import com.erp.hrm.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Controller
@RequestMapping("/hrm/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HRM_VIEW')")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("attendance", attendanceService.byDate(date,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "date"))));
        model.addAttribute("date", date);
        model.addAttribute("pageTitle", "Attendance");
        return "hrm/attendance/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String markForm(Model model) {
        model.addAttribute("attendance", new Attendance());
        model.addAttribute("employees", employeeService.all());
        model.addAttribute("statuses", AttendanceStatus.values());
        model.addAttribute("pageTitle", "Mark Attendance");
        return "hrm/attendance/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String save(@ModelAttribute("attendance") Attendance attendance,
                       @RequestParam(value = "employeeId", required = false) UUID employeeId,
                       @RequestParam(value = "checkIn", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime checkIn,
                       @RequestParam(value = "checkOut", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime checkOut,
                       RedirectAttributes ra) {
        attendance.setEmployee(employeeId != null ? employeeService.get(employeeId) : null);
        attendance.setCheckIn(checkIn);
        attendance.setCheckOut(checkOut);
        attendanceService.save(attendance);
        ra.addFlashAttribute("successMessage", "Attendance recorded.");
        return "redirect:/hrm/attendance";
    }
}
