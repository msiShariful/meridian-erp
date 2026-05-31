package com.erp.hrm.controller;

import com.erp.hrm.entity.Department;
import com.erp.hrm.service.DepartmentService;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/hrm/departments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HRM_VIEW')")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        var departments = departmentService.search(q, PageRequest.of(page, 20, Sort.by("name")));
        Map<UUID, Long> counts = new LinkedHashMap<>();
        departments.getContent().forEach(d -> counts.put(d.getId(), departmentService.employeeCount(d.getId())));
        model.addAttribute("departments", departments);
        model.addAttribute("counts", counts);
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Departments");
        return "hrm/departments/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("pageTitle", "New Department");
        return "hrm/departments/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("department", departmentService.get(id));
        model.addAttribute("pageTitle", "Edit Department");
        return "hrm/departments/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String save(@Valid @ModelAttribute("department") Department department, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", department.isNew() ? "New Department" : "Edit Department");
            return "hrm/departments/form";
        }
        departmentService.save(department);
        ra.addFlashAttribute("successMessage", "Department saved successfully.");
        return "redirect:/hrm/departments";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        departmentService.delete(id);
        ra.addFlashAttribute("successMessage", "Department deleted.");
        return "redirect:/hrm/departments";
    }
}
