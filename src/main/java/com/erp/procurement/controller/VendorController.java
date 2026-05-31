package com.erp.procurement.controller;

import com.erp.procurement.entity.Vendor;
import com.erp.procurement.enums.VendorStatus;
import com.erp.procurement.service.PurchaseOrderService;
import com.erp.procurement.service.VendorBillService;
import com.erp.procurement.service.VendorService;
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
@RequestMapping("/procurement/vendors")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PROCUREMENT_VIEW')")
public class VendorController {

    private final VendorService vendorService;
    private final PurchaseOrderService purchaseOrderService;
    private final VendorBillService vendorBillService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("vendors", vendorService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "name"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Vendors");
        return "procurement/vendors/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("vendor", new Vendor());
        prepareForm(model);
        model.addAttribute("pageTitle", "New Vendor");
        return "procurement/vendors/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("vendor", vendorService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Vendor");
        return "procurement/vendors/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String save(@Valid @ModelAttribute("vendor") Vendor vendor, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", vendor.isNew() ? "New Vendor" : "Edit Vendor");
            return "procurement/vendors/form";
        }
        vendorService.save(vendor);
        ra.addFlashAttribute("successMessage", "Vendor saved successfully.");
        return "redirect:/procurement/vendors";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Vendor vendor = vendorService.get(id);
        model.addAttribute("vendor", vendor);
        model.addAttribute("orders", purchaseOrderService.forVendor(vendor));
        model.addAttribute("bills", vendorBillService.forVendor(id));
        model.addAttribute("pageTitle", vendor.getName());
        return "procurement/vendors/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        vendorService.delete(id);
        ra.addFlashAttribute("successMessage", "Vendor deleted.");
        return "redirect:/procurement/vendors";
    }

    private void prepareForm(Model model) {
        model.addAttribute("statuses", VendorStatus.values());
        model.addAttribute("ratings", Arrays.asList(1, 2, 3, 4, 5));
    }
}
