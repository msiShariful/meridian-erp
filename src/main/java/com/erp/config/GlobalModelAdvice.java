package com.erp.config;

import com.erp.core.service.CustomUserDetails;
import com.erp.notifications.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes commonly-needed values to every Thymeleaf view: the current request URI
 * (for sidebar active-state highlighting), the authenticated principal, and the
 * unread-notification badge count.
 */
@ControllerAdvice(basePackages = "com.erp")
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final NotificationService notificationService;

    @ModelAttribute
    public void addGlobals(Model model, HttpServletRequest request,
                           @AuthenticationPrincipal CustomUserDetails principal) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("appName", "Meridian ERP");
        if (principal != null) {
            model.addAttribute("currentUser", principal);
            model.addAttribute("notificationCount", notificationService.unreadCount(principal.getUsername()));
        }
    }
}
