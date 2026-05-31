package com.erp.crm.controller;

import com.erp.crm.entity.Activity;
import com.erp.crm.enums.ActivityType;
import com.erp.crm.service.ActivityService;
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
@RequestMapping("/crm/activities")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CRM_VIEW')")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("activities", activityService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Activities");
        return "crm/activities/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("types", ActivityType.values());
        model.addAttribute("pageTitle", "Log Activity");
        return "crm/activities/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String save(@Valid @ModelAttribute("activity") Activity activity, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("types", ActivityType.values());
            model.addAttribute("pageTitle", "Log Activity");
            return "crm/activities/form";
        }
        activityService.save(activity);
        ra.addFlashAttribute("successMessage", "Activity logged.");
        return "redirect:/crm/activities";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/toggle")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String toggle(@PathVariable UUID id, RedirectAttributes ra) {
        activityService.toggleComplete(id);
        return "redirect:/crm/activities";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        activityService.delete(id);
        ra.addFlashAttribute("successMessage", "Activity deleted.");
        return "redirect:/crm/activities";
    }
}
