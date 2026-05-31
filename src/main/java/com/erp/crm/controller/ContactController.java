package com.erp.crm.controller;

import com.erp.crm.entity.Contact;
import com.erp.crm.service.CompanyService;
import com.erp.crm.service.ContactService;
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
@RequestMapping("/crm/contacts")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CRM_VIEW')")
public class ContactController {

    private final ContactService contactService;
    private final CompanyService companyService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("contacts", contactService.search(q, PageRequest.of(page, 20, Sort.by("firstName"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Contacts");
        return "crm/contacts/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("contact", new Contact());
        model.addAttribute("companies", companyService.all());
        model.addAttribute("pageTitle", "New Contact");
        return "crm/contacts/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("contact", contactService.get(id));
        model.addAttribute("companies", companyService.all());
        model.addAttribute("pageTitle", "Edit Contact");
        return "crm/contacts/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String save(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
                       @RequestParam(value = "companyId", required = false) UUID companyId,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("companies", companyService.all());
            model.addAttribute("pageTitle", contact.isNew() ? "New Contact" : "Edit Contact");
            return "crm/contacts/form";
        }
        contact.setCompany(companyId != null ? companyService.get(companyId) : null);
        contactService.save(contact);
        ra.addFlashAttribute("successMessage", "Contact saved successfully.");
        return "redirect:/crm/contacts";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Contact contact = contactService.get(id);
        model.addAttribute("contact", contact);
        model.addAttribute("pageTitle", contact.getFullName());
        return "crm/contacts/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        contactService.delete(id);
        ra.addFlashAttribute("successMessage", "Contact deleted.");
        return "redirect:/crm/contacts";
    }
}
