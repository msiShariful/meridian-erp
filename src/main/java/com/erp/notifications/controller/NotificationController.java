package com.erp.notifications.controller;

import com.erp.core.service.CustomUserDetails;
import com.erp.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails principal,
                       @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
                       Model model) {
        String email = principal != null ? principal.getUsername() : null;
        model.addAttribute("notifications", notificationService.forUser(email, PageRequest.of(page, 30)));
        model.addAttribute("pageTitle", "Notifications");
        return "notifications/index";
    }

    @PostMapping("/read-all")
    public String readAll(@AuthenticationPrincipal CustomUserDetails principal, RedirectAttributes ra) {
        if (principal != null) {
            notificationService.markAllRead(principal.getUsername());
        }
        ra.addFlashAttribute("successMessage", "All notifications marked as read.");
        return "redirect:/notifications";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/read")
    public String read(@PathVariable UUID id) {
        notificationService.markRead(id);
        return "redirect:/notifications";
    }
}
