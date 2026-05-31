package com.erp.crm.controller;

import com.erp.crm.entity.Campaign;
import com.erp.crm.enums.CampaignStatus;
import com.erp.crm.enums.CampaignType;
import com.erp.crm.service.CampaignService;
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
@RequestMapping("/crm/campaigns")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CRM_VIEW')")
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("campaigns", campaignService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Campaigns");
        return "crm/campaigns/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("campaign", new Campaign());
        prepareForm(model);
        model.addAttribute("pageTitle", "New Campaign");
        return "crm/campaigns/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("campaign", campaignService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Campaign");
        return "crm/campaigns/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String save(@Valid @ModelAttribute("campaign") Campaign campaign, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", campaign.isNew() ? "New Campaign" : "Edit Campaign");
            return "crm/campaigns/form";
        }
        campaignService.save(campaign);
        ra.addFlashAttribute("successMessage", "Campaign saved successfully.");
        return "redirect:/crm/campaigns";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Campaign campaign = campaignService.get(id);
        model.addAttribute("campaign", campaign);
        model.addAttribute("pageTitle", campaign.getName());
        return "crm/campaigns/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        campaignService.delete(id);
        ra.addFlashAttribute("successMessage", "Campaign deleted.");
        return "redirect:/crm/campaigns";
    }

    private void prepareForm(Model model) {
        model.addAttribute("types", CampaignType.values());
        model.addAttribute("statuses", CampaignStatus.values());
    }
}
