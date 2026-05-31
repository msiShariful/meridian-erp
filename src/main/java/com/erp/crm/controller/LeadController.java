package com.erp.crm.controller;

import com.erp.crm.entity.Lead;
import com.erp.crm.enums.LeadSource;
import com.erp.crm.enums.LeadStatus;
import com.erp.crm.service.ActivityService;
import com.erp.crm.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/crm/leads")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CRM_VIEW')")
public class LeadController {

    private final LeadService leadService;
    private final ActivityService activityService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) LeadStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        Page<Lead> leads = leadService.search(q, status,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("leads", leads);
        model.addAttribute("q", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", LeadStatus.values());
        model.addAttribute("pageTitle", "Leads");
        return "crm/leads/list";
    }

    @GetMapping("/kanban")
    public String kanban(Model model) {
        Map<LeadStatus, java.util.List<Lead>> board = new LinkedHashMap<>();
        for (LeadStatus s : LeadStatus.pipeline()) {
            board.put(s, leadService.byStatus(s));
        }
        model.addAttribute("board", board);
        model.addAttribute("pageTitle", "Lead Pipeline");
        return "crm/leads/kanban";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("lead", new Lead());
        prepareForm(model);
        model.addAttribute("pageTitle", "New Lead");
        return "crm/leads/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("lead", leadService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Lead");
        return "crm/leads/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String save(@Valid @ModelAttribute("lead") Lead lead, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", lead.isNew() ? "New Lead" : "Edit Lead");
            return "crm/leads/form";
        }
        leadService.save(lead);
        ra.addFlashAttribute("successMessage", "Lead saved successfully.");
        return "redirect:/crm/leads";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Lead lead = leadService.get(id);
        model.addAttribute("lead", lead);
        model.addAttribute("activities", activityService.forRecord(id));
        model.addAttribute("pageTitle", lead.getName());
        return "crm/leads/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/status")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    @ResponseBody
    public String updateStatus(@PathVariable UUID id, @RequestParam LeadStatus status) {
        leadService.updateStatus(id, status);
        return "ok";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/convert")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String convert(@PathVariable UUID id, RedirectAttributes ra) {
        var deal = leadService.convert(id);
        ra.addFlashAttribute("successMessage", "Lead converted to contact and deal.");
        return "redirect:/crm/deals/" + deal.getId();
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        leadService.delete(id);
        ra.addFlashAttribute("successMessage", "Lead deleted.");
        return "redirect:/crm/leads";
    }

    private void prepareForm(Model model) {
        model.addAttribute("sources", LeadSource.values());
        model.addAttribute("statuses", LeadStatus.values());
        model.addAttribute("scores", Arrays.asList(1, 2, 3, 4, 5));
    }
}
