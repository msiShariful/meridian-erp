package com.erp.accounting.controller;

import com.erp.accounting.entity.JournalEntry;
import com.erp.accounting.service.AccountService;
import com.erp.accounting.service.JournalEntryForm;
import com.erp.accounting.service.JournalService;
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
@RequestMapping("/accounting/journal")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ACCOUNTING_VIEW')")
public class JournalController {

    private static final int LINE_ROWS = 4;

    private final JournalService journalService;
    private final AccountService accountService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("entries", journalService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "date"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Journal Entries");
        return "accounting/journal/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String createForm(Model model) {
        JournalEntryForm form = new JournalEntryForm();
        while (form.getLines().size() < LINE_ROWS) {
            form.getLines().add(new JournalEntryForm.Line());
        }
        model.addAttribute("journalForm", form);
        model.addAttribute("accounts", accountService.all());
        model.addAttribute("pageTitle", "New Journal Entry");
        return "accounting/journal/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String save(@ModelAttribute("journalForm") JournalEntryForm form, RedirectAttributes ra) {
        JournalEntry saved = journalService.save(form);
        ra.addFlashAttribute("successMessage", "Journal entry posted.");
        return "redirect:/accounting/journal/" + saved.getId();
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        JournalEntry entry = journalService.get(id);
        model.addAttribute("entry", entry);
        model.addAttribute("pageTitle", entry.getReference() != null ? entry.getReference() : "Journal Entry");
        return "accounting/journal/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        journalService.delete(id);
        ra.addFlashAttribute("successMessage", "Journal entry deleted.");
        return "redirect:/accounting/journal";
    }
}
