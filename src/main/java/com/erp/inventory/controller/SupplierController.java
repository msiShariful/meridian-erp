package com.erp.inventory.controller;

import com.erp.inventory.entity.Supplier;
import com.erp.inventory.service.SupplierService;
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

import java.util.Arrays;
import java.util.UUID;

@Controller
@RequestMapping("/inventory/suppliers")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('INVENTORY_VIEW')")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("suppliers", supplierService.search(q, PageRequest.of(page, 20, Sort.by("name"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Suppliers");
        return "inventory/suppliers/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("ratings", Arrays.asList(1, 2, 3, 4, 5));
        model.addAttribute("pageTitle", "New Supplier");
        return "inventory/suppliers/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("supplier", supplierService.get(id));
        model.addAttribute("ratings", Arrays.asList(1, 2, 3, 4, 5));
        model.addAttribute("pageTitle", "Edit Supplier");
        return "inventory/suppliers/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String save(@Valid @ModelAttribute("supplier") Supplier supplier, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("ratings", Arrays.asList(1, 2, 3, 4, 5));
            model.addAttribute("pageTitle", supplier.isNew() ? "New Supplier" : "Edit Supplier");
            return "inventory/suppliers/form";
        }
        supplierService.save(supplier);
        ra.addFlashAttribute("successMessage", "Supplier saved successfully.");
        return "redirect:/inventory/suppliers";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Supplier supplier = supplierService.get(id);
        model.addAttribute("supplier", supplier);
        model.addAttribute("pageTitle", supplier.getName());
        return "inventory/suppliers/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        supplierService.delete(id);
        ra.addFlashAttribute("successMessage", "Supplier deleted.");
        return "redirect:/inventory/suppliers";
    }
}
