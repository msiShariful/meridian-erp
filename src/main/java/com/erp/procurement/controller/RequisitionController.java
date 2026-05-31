package com.erp.procurement.controller;

import com.erp.procurement.entity.Requisition;
import com.erp.procurement.enums.RequisitionStatus;
import com.erp.procurement.service.RequisitionService;
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
@RequestMapping("/procurement/requisitions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PROCUREMENT_VIEW')")
public class RequisitionController {

    private final RequisitionService requisitionService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("requisitions", requisitionService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Requisitions");
        return "procurement/requisitions/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("requisition", new Requisition());
        prepareForm(model);
        model.addAttribute("pageTitle", "New Requisition");
        return "procurement/requisitions/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("requisition", requisitionService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Requisition");
        return "procurement/requisitions/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String save(@Valid @ModelAttribute("requisition") Requisition requisition, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", requisition.isNew() ? "New Requisition" : "Edit Requisition");
            return "procurement/requisitions/form";
        }
        requisitionService.save(requisition);
        ra.addFlashAttribute("successMessage", "Requisition saved successfully.");
        return "redirect:/procurement/requisitions";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Requisition req = requisitionService.get(id);
        model.addAttribute("requisition", req);
        model.addAttribute("pageTitle", req.getReqNumber());
        return "procurement/requisitions/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/submit")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String submit(@PathVariable UUID id, RedirectAttributes ra) {
        requisitionService.updateStatus(id, RequisitionStatus.SUBMITTED);
        ra.addFlashAttribute("successMessage", "Requisition submitted for approval.");
        return "redirect:/procurement/requisitions/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/approve")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String approve(@PathVariable UUID id, RedirectAttributes ra) {
        requisitionService.updateStatus(id, RequisitionStatus.APPROVED);
        ra.addFlashAttribute("successMessage", "Requisition approved.");
        return "redirect:/procurement/requisitions/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/reject")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String reject(@PathVariable UUID id, RedirectAttributes ra) {
        requisitionService.updateStatus(id, RequisitionStatus.REJECTED);
        ra.addFlashAttribute("successMessage", "Requisition rejected.");
        return "redirect:/procurement/requisitions/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('PROCUREMENT_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        requisitionService.delete(id);
        ra.addFlashAttribute("successMessage", "Requisition deleted.");
        return "redirect:/procurement/requisitions";
    }

    private void prepareForm(Model model) {
        model.addAttribute("statuses", RequisitionStatus.values());
    }
}
