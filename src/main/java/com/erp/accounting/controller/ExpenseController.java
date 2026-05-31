package com.erp.accounting.controller;

import com.erp.accounting.entity.Expense;
import com.erp.accounting.enums.ExpenseStatus;
import com.erp.accounting.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/accounting/expenses")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ACCOUNTING_VIEW')")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("expenses", expenseService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "date"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Expenses");
        return "accounting/expenses/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String createForm(Model model) {
        Expense expense = new Expense();
        expense.setDate(LocalDate.now());
        model.addAttribute("expense", expense);
        prepareForm(model);
        model.addAttribute("pageTitle", "New Expense Claim");
        return "accounting/expenses/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        Expense expense = expenseService.get(id);
        model.addAttribute("expense", expense);
        model.addAttribute("selectedCategoryId", expense.getCategory() != null ? expense.getCategory().getId() : null);
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Expense Claim");
        return "accounting/expenses/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String save(@ModelAttribute("expense") Expense expense,
                       @RequestParam(value = "categoryId", required = false) UUID categoryId,
                       RedirectAttributes ra) {
        expense.setCategory(categoryId != null ? expenseService.category(categoryId) : null);
        expenseService.save(expense);
        ra.addFlashAttribute("successMessage", "Expense claim saved.");
        return "redirect:/accounting/expenses";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/approve")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String approve(@PathVariable UUID id, RedirectAttributes ra) {
        expenseService.updateStatus(id, ExpenseStatus.APPROVED);
        ra.addFlashAttribute("successMessage", "Expense approved.");
        return "redirect:/accounting/expenses";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/reject")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String reject(@PathVariable UUID id, RedirectAttributes ra) {
        expenseService.updateStatus(id, ExpenseStatus.REJECTED);
        ra.addFlashAttribute("successMessage", "Expense rejected.");
        return "redirect:/accounting/expenses";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        expenseService.delete(id);
        ra.addFlashAttribute("successMessage", "Expense deleted.");
        return "redirect:/accounting/expenses";
    }

    private void prepareForm(Model model) {
        model.addAttribute("categories", expenseService.categories());
        model.addAttribute("statuses", ExpenseStatus.values());
    }
}
