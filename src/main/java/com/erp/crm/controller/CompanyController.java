package com.erp.crm.controller;

import com.erp.common.util.FileStorageService;
import com.erp.crm.entity.Company;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/crm/companies")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CRM_VIEW')")
public class CompanyController {

    private final CompanyService companyService;
    private final ContactService contactService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("companies", companyService.search(q, PageRequest.of(page, 20, Sort.by("name"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Companies");
        return "crm/companies/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("company", new Company());
        model.addAttribute("pageTitle", "New Company");
        return "crm/companies/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("company", companyService.get(id));
        model.addAttribute("pageTitle", "Edit Company");
        return "crm/companies/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String save(@Valid @ModelAttribute("company") Company company, BindingResult result,
                       @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", company.isNew() ? "New Company" : "Edit Company");
            return "crm/companies/form";
        }
        if (logoFile != null && !logoFile.isEmpty()) {
            company.setLogo(fileStorageService.store(logoFile, "companies"));
        }
        companyService.save(company);
        ra.addFlashAttribute("successMessage", "Company saved successfully.");
        return "redirect:/crm/companies";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Company company = companyService.get(id);
        model.addAttribute("company", company);
        model.addAttribute("contacts", contactService.byCompany(id));
        model.addAttribute("pageTitle", company.getName());
        return "crm/companies/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        companyService.delete(id);
        ra.addFlashAttribute("successMessage", "Company deleted.");
        return "redirect:/crm/companies";
    }
}
