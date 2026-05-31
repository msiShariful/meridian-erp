package com.erp.accounting.controller;

import com.erp.accounting.entity.Account;
import com.erp.accounting.enums.AccountType;
import com.erp.accounting.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/accounting/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ACCOUNTING_VIEW')")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        List<Account> accounts = accountService.all();
        Map<AccountType, List<Account>> grouped = new LinkedHashMap<>();
        for (AccountType type : AccountType.values()) {
            grouped.put(type, accounts.stream()
                    .filter(a -> a.getType() == type
                            && (q == null || q.isBlank()
                            || a.getName().toLowerCase().contains(q.toLowerCase())
                            || a.getCode().toLowerCase().contains(q.toLowerCase())))
                    .toList());
        }
        model.addAttribute("grouped", grouped);
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Chart of Accounts");
        return "accounting/accounts/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("types", AccountType.values());
        model.addAttribute("pageTitle", "New Account");
        return "accounting/accounts/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("account", accountService.get(id));
        model.addAttribute("types", AccountType.values());
        model.addAttribute("pageTitle", "Edit Account");
        return "accounting/accounts/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String save(@Valid @ModelAttribute("account") Account account, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("types", AccountType.values());
            model.addAttribute("pageTitle", account.isNew() ? "New Account" : "Edit Account");
            return "accounting/accounts/form";
        }
        accountService.save(account);
        ra.addFlashAttribute("successMessage", "Account saved successfully.");
        return "redirect:/accounting/accounts";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        accountService.delete(id);
        ra.addFlashAttribute("successMessage", "Account deleted.");
        return "redirect:/accounting/accounts";
    }
}
