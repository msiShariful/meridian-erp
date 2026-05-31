package com.erp.ecommerce.controller;

import com.erp.ecommerce.enums.ReviewStatus;
import com.erp.ecommerce.service.ReviewService;
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
@RequestMapping("/ecommerce/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ECOMMERCE_VIEW')")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) ReviewStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("reviews", reviewService.search(q, status,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("q", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", ReviewStatus.values());
        model.addAttribute("pageTitle", "Reviews");
        return "ecommerce/reviews/list";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/approve")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String approve(@PathVariable UUID id, RedirectAttributes ra) {
        reviewService.updateStatus(id, ReviewStatus.APPROVED);
        ra.addFlashAttribute("successMessage", "Review approved.");
        return "redirect:/ecommerce/reviews";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/reject")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String reject(@PathVariable UUID id, RedirectAttributes ra) {
        reviewService.updateStatus(id, ReviewStatus.REJECTED);
        ra.addFlashAttribute("successMessage", "Review rejected.");
        return "redirect:/ecommerce/reviews";
    }
}
