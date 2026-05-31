package com.erp.hrm.controller;

import com.erp.hrm.entity.Training;
import com.erp.hrm.service.TrainingService;
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
@RequestMapping("/hrm/training")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HRM_VIEW')")
public class TrainingController {

    private final TrainingService trainingService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("trainings", trainingService.search(q, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "startDate"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Training");
        return "hrm/training/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("training", new Training());
        prepareForm(model);
        model.addAttribute("pageTitle", "New Training");
        return "hrm/training/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("training", trainingService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Training");
        return "hrm/training/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String save(@Valid @ModelAttribute("training") Training training, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", training.isNew() ? "New Training" : "Edit Training");
            return "hrm/training/form";
        }
        trainingService.save(training);
        ra.addFlashAttribute("successMessage", "Training saved successfully.");
        return "redirect:/hrm/training";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('HRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        trainingService.delete(id);
        ra.addFlashAttribute("successMessage", "Training deleted.");
        return "redirect:/hrm/training";
    }

    private void prepareForm(Model model) {
        model.addAttribute("statuses", Arrays.asList("PLANNED", "ONGOING", "COMPLETED", "CANCELLED"));
    }
}
