package com.erp.support.controller;

import com.erp.support.entity.FAQ;
import com.erp.support.service.FAQService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/support/faq")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SUPPORT_VIEW')")
public class FaqController {

    private final FAQService faqService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("groups", faqService.grouped());
        model.addAttribute("pageTitle", "FAQ");
        return "support/faq/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("faq", new FAQ());
        model.addAttribute("pageTitle", "New FAQ");
        return "support/faq/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("faq", faqService.get(id));
        model.addAttribute("pageTitle", "Edit FAQ");
        return "support/faq/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String save(@Valid @ModelAttribute("faq") FAQ faq, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", faq.isNew() ? "New FAQ" : "Edit FAQ");
            return "support/faq/form";
        }
        faqService.save(faq);
        ra.addFlashAttribute("successMessage", "FAQ saved successfully.");
        return "redirect:/support/faq";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        faqService.delete(id);
        ra.addFlashAttribute("successMessage", "FAQ deleted.");
        return "redirect:/support/faq";
    }
}
