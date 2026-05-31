package com.erp.hrm.controller;

import com.erp.hrm.entity.Payroll;
import com.erp.hrm.enums.PayrollStatus;
import com.erp.hrm.service.EmployeeService;
import com.erp.hrm.service.PayrollService;
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
@RequestMapping("/hrm/payroll")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HRM_VIEW')")
public class PayrollController {

    private final PayrollService payrollService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("payrolls", payrollService.list(PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "period"))));
        model.addAttribute("pageTitle", "Payroll");
        return "hrm/payroll/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String generateForm(Model model) {
        model.addAttribute("payroll", new Payroll());
        prepareForm(model);
        model.addAttribute("pageTitle", "Generate Payroll");
        return "hrm/payroll/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String save(@ModelAttribute("payroll") Payroll payroll,
                       @RequestParam(value = "employeeId", required = false) UUID employeeId,
                       RedirectAttributes ra) {
        payroll.setEmployee(employeeId != null ? employeeService.get(employeeId) : null);
        payrollService.save(payroll);
        ra.addFlashAttribute("successMessage", "Payroll generated.");
        return "redirect:/hrm/payroll";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Payroll payroll = payrollService.get(id);
        model.addAttribute("payroll", payroll);
        model.addAttribute("pageTitle", "Payslip");
        return "hrm/payroll/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        payrollService.delete(id);
        ra.addFlashAttribute("successMessage", "Payroll deleted.");
        return "redirect:/hrm/payroll";
    }

    private void prepareForm(Model model) {
        model.addAttribute("employees", employeeService.all());
        model.addAttribute("statuses", PayrollStatus.values());
    }
}
