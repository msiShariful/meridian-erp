package com.erp.hrm.controller;

import com.erp.hrm.entity.PerformanceReview;
import com.erp.hrm.service.EmployeeService;
import com.erp.hrm.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.UUID;

@Controller
@RequestMapping("/hrm/performance")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HRM_VIEW')")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("reviews", performanceService.list(PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("pageTitle", "Performance Reviews");
        return "hrm/performance/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("review", new PerformanceReview());
        prepareForm(model);
        model.addAttribute("pageTitle", "New Performance Review");
        return "hrm/performance/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("review", performanceService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Performance Review");
        return "hrm/performance/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String save(@ModelAttribute("review") PerformanceReview review,
                       @RequestParam(value = "employeeId", required = false) UUID employeeId,
                       RedirectAttributes ra) {
        review.setEmployee(employeeId != null ? employeeService.get(employeeId) : null);
        performanceService.save(review);
        ra.addFlashAttribute("successMessage", "Performance review saved.");
        return "redirect:/hrm/performance";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        performanceService.delete(id);
        ra.addFlashAttribute("successMessage", "Performance review deleted.");
        return "redirect:/hrm/performance";
    }

    private void prepareForm(Model model) {
        model.addAttribute("employees", employeeService.all());
        model.addAttribute("ratings", Arrays.asList(1, 2, 3, 4, 5));
    }
}
