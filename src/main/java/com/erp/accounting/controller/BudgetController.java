package com.erp.accounting.controller;

import com.erp.accounting.entity.Budget;
import com.erp.accounting.service.BudgetService;
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
@RequestMapping("/accounting/budget")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ACCOUNTING_VIEW')")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("budgets", budgetService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "categoryName"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Budgets");
        return "accounting/budget/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("budget", new Budget());
        model.addAttribute("pageTitle", "New Budget");
        return "accounting/budget/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("budget", budgetService.get(id));
        model.addAttribute("pageTitle", "Edit Budget");
        return "accounting/budget/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String save(@Valid @ModelAttribute("budget") Budget budget, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", budget.isNew() ? "New Budget" : "Edit Budget");
            return "accounting/budget/form";
        }
        budgetService.save(budget);
        ra.addFlashAttribute("successMessage", "Budget saved.");
        return "redirect:/accounting/budget";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        budgetService.delete(id);
        ra.addFlashAttribute("successMessage", "Budget deleted.");
        return "redirect:/accounting/budget";
    }
}
