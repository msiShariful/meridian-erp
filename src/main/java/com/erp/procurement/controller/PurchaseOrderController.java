package com.erp.procurement.controller;

import com.erp.procurement.entity.PurchaseOrder;
import com.erp.procurement.service.GRNService;
import com.erp.procurement.service.PurchaseOrderForm;
import com.erp.procurement.service.PurchaseOrderService;
import com.erp.procurement.service.VendorService;
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
@RequestMapping("/procurement/purchase-orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PROCUREMENT_VIEW')")
public class PurchaseOrderController {

    private static final int ITEM_ROWS = 5;

    private final PurchaseOrderService purchaseOrderService;
    private final VendorService vendorService;
    private final GRNService grnService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("orders", purchaseOrderService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "orderDate"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Purchase Orders");
        return "procurement/purchase-orders/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String createForm(Model model) {
        PurchaseOrderForm form = new PurchaseOrderForm();
        padItems(form);
        model.addAttribute("poForm", form);
        model.addAttribute("vendors", vendorService.all());
        model.addAttribute("pageTitle", "New Purchase Order");
        return "procurement/purchase-orders/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        PurchaseOrderForm form = purchaseOrderService.toForm(purchaseOrderService.get(id));
        padItems(form);
        model.addAttribute("poForm", form);
        model.addAttribute("vendors", vendorService.all());
        model.addAttribute("pageTitle", "Edit Purchase Order");
        return "procurement/purchase-orders/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String save(@ModelAttribute("poForm") PurchaseOrderForm form, RedirectAttributes ra) {
        PurchaseOrder saved = purchaseOrderService.save(form);
        ra.addFlashAttribute("successMessage", "Purchase order " + saved.getPoNumber() + " saved.");
        return "redirect:/procurement/purchase-orders/" + saved.getId();
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        PurchaseOrder po = purchaseOrderService.get(id);
        model.addAttribute("po", po);
        model.addAttribute("pageTitle", po.getPoNumber());
        return "procurement/purchase-orders/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/status")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String send(@PathVariable UUID id, RedirectAttributes ra) {
        purchaseOrderService.send(id);
        ra.addFlashAttribute("successMessage", "Purchase order sent to vendor.");
        return "redirect:/procurement/purchase-orders/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/receive")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String receive(@PathVariable UUID id,
                          @RequestParam(required = false) String receivedBy,
                          @RequestParam(defaultValue = "false") boolean partial,
                          @RequestParam(required = false) String notes,
                          RedirectAttributes ra) {
        var grn = purchaseOrderService.receive(id, receivedBy, partial, notes);
        ra.addFlashAttribute("successMessage", "Goods received. " + grn.getGrnNumber() + " created.");
        return "redirect:/procurement/purchase-orders/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/close")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String close(@PathVariable UUID id, RedirectAttributes ra) {
        purchaseOrderService.close(id);
        ra.addFlashAttribute("successMessage", "Purchase order closed.");
        return "redirect:/procurement/purchase-orders/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        purchaseOrderService.delete(id);
        ra.addFlashAttribute("successMessage", "Purchase order deleted.");
        return "redirect:/procurement/purchase-orders";
    }

    private void padItems(PurchaseOrderForm form) {
        while (form.getItems().size() < ITEM_ROWS) {
            form.getItems().add(new PurchaseOrderForm.Item());
        }
    }
}
