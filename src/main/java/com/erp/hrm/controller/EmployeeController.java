package com.erp.hrm.controller;

import com.erp.hrm.entity.Employee;
import com.erp.hrm.enums.EmployeeStatus;
import com.erp.hrm.service.DepartmentService;
import com.erp.hrm.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/hrm/employees")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HRM_VIEW')")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("employees", employeeService.search(q, PageRequest.of(page, 20, Sort.by("firstName"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Employees");
        return "hrm/employees/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("employee", new Employee());
        prepareForm(model);
        model.addAttribute("pageTitle", "New Employee");
        return "hrm/employees/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("employee", employeeService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Employee");
        return "hrm/employees/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String save(@Valid @ModelAttribute("employee") Employee employee, BindingResult result,
                       @RequestParam(value = "departmentId", required = false) UUID departmentId,
                       @RequestParam(value = "designationId", required = false) UUID designationId,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", employee.isNew() ? "New Employee" : "Edit Employee");
            return "hrm/employees/form";
        }
        employee.setDepartment(departmentId != null ? departmentService.get(departmentId) : null);
        employee.setDesignation(designationId != null ? departmentService.designations().stream()
                .filter(d -> d.getId().equals(designationId)).findFirst().orElse(null) : null);
        employeeService.save(employee);
        ra.addFlashAttribute("successMessage", "Employee saved successfully.");
        return "redirect:/hrm/employees";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Employee employee = employeeService.get(id);
        model.addAttribute("employee", employee);
        model.addAttribute("pageTitle", employee.getFullName());
        return "hrm/employees/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        employeeService.delete(id);
        ra.addFlashAttribute("successMessage", "Employee deleted.");
        return "redirect:/hrm/employees";
    }

    private void prepareForm(Model model) {
        model.addAttribute("departments", departmentService.all());
        model.addAttribute("designations", departmentService.designations());
        model.addAttribute("statuses", EmployeeStatus.values());
    }
}
