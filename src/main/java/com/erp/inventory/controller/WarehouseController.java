package com.erp.inventory.controller;

import com.erp.inventory.entity.Warehouse;
import com.erp.inventory.service.WarehouseService;
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
@RequestMapping("/inventory/warehouses")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('INVENTORY_VIEW')")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("warehouses", warehouseService.search(q, PageRequest.of(page, 20, Sort.by("name"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Warehouses");
        return "inventory/warehouses/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        model.addAttribute("pageTitle", "New Warehouse");
        return "inventory/warehouses/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("warehouse", warehouseService.get(id));
        model.addAttribute("pageTitle", "Edit Warehouse");
        return "inventory/warehouses/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String save(@Valid @ModelAttribute("warehouse") Warehouse warehouse, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", warehouse.isNew() ? "New Warehouse" : "Edit Warehouse");
            return "inventory/warehouses/form";
        }
        warehouseService.save(warehouse);
        ra.addFlashAttribute("successMessage", "Warehouse saved successfully.");
        return "redirect:/inventory/warehouses";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        warehouseService.delete(id);
        ra.addFlashAttribute("successMessage", "Warehouse deleted.");
        return "redirect:/inventory/warehouses";
    }
}
