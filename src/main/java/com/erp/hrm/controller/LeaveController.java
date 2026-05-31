package com.erp.hrm.controller;

import com.erp.hrm.entity.Leave;
import com.erp.hrm.enums.LeaveStatus;
import com.erp.hrm.service.EmployeeService;
import com.erp.hrm.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/hrm/leave")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HRM_VIEW')")
public class LeaveController {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("leaves", leaveService.list(PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "startDate"))));
        model.addAttribute("pageTitle", "Leave Requests");
        return "hrm/leave/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String applyForm(Model model) {
        model.addAttribute("leave", new Leave());
        prepareForm(model);
        model.addAttribute("pageTitle", "Apply for Leave");
        return "hrm/leave/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String save(@ModelAttribute("leave") Leave leave,
                       @RequestParam(value = "employeeId", required = false) UUID employeeId,
                       @RequestParam(value = "leaveTypeId", required = false) UUID leaveTypeId,
                       RedirectAttributes ra) {
        leave.setEmployee(employeeId != null ? employeeService.get(employeeId) : null);
        leave.setLeaveType(leaveTypeId != null ? leaveService.leaveType(leaveTypeId) : null);
        leaveService.save(leave);
        ra.addFlashAttribute("successMessage", "Leave request submitted.");
        return "redirect:/hrm/leave";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/approve")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String approve(@PathVariable UUID id, RedirectAttributes ra) {
        leaveService.updateStatus(id, LeaveStatus.APPROVED);
        ra.addFlashAttribute("successMessage", "Leave approved.");
        return "redirect:/hrm/leave";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/reject")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String reject(@PathVariable UUID id, RedirectAttributes ra) {
        leaveService.updateStatus(id, LeaveStatus.REJECTED);
        ra.addFlashAttribute("successMessage", "Leave rejected.");
        return "redirect:/hrm/leave";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        leaveService.delete(id);
        ra.addFlashAttribute("successMessage", "Leave request deleted.");
        return "redirect:/hrm/leave";
    }

    private void prepareForm(Model model) {
        model.addAttribute("employees", employeeService.all());
        model.addAttribute("leaveTypes", leaveService.leaveTypes());
    }
}
