package com.erp.procurement.controller;

import com.erp.procurement.entity.GRN;
import com.erp.procurement.enums.GRNStatus;
import com.erp.procurement.service.GRNService;
import com.erp.procurement.service.PurchaseOrderService;
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
@RequestMapping("/procurement/grn")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PROCUREMENT_VIEW')")
public class GRNController {

    private final GRNService grnService;
    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("grns", grnService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "receivedDate"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Goods Receipt Notes");
        return "procurement/grn/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("grn", new GRN());
        prepareForm(model);
        model.addAttribute("pageTitle", "New GRN");
        return "procurement/grn/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("grn", grnService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit GRN");
        return "procurement/grn/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String save(@Valid @ModelAttribute("grn") GRN grn, BindingResult result,
                       @RequestParam(value = "purchaseOrderId", required = false) UUID purchaseOrderId,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", grn.isNew() ? "New GRN" : "Edit GRN");
            return "procurement/grn/form";
        }
        grn.setPurchaseOrder(purchaseOrderId != null ? purchaseOrderService.get(purchaseOrderId) : null);
        grnService.save(grn);
        ra.addFlashAttribute("successMessage", "GRN saved successfully.");
        return "redirect:/procurement/grn";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        GRN grn = grnService.get(id);
        model.addAttribute("grn", grn);
        model.addAttribute("pageTitle", grn.getGrnNumber());
        return "procurement/grn/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        grnService.delete(id);
        ra.addFlashAttribute("successMessage", "GRN deleted.");
        return "redirect:/procurement/grn";
    }

    private void prepareForm(Model model) {
        model.addAttribute("statuses", GRNStatus.values());
        model.addAttribute("orders", purchaseOrderService.search(null,
                PageRequest.of(0, 200, Sort.by(Sort.Direction.DESC, "orderDate"))).getContent());
    }
}
