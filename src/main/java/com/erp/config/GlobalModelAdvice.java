package com.erp.config;

import com.erp.core.service.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes commonly-needed values to every Thymeleaf view: the current request URI
 * (for sidebar active-state highlighting) and the authenticated principal.
 */
@ControllerAdvice(basePackages = "com.erp")
public class GlobalModelAdvice {

    @ModelAttribute
    public void addGlobals(Model model, HttpServletRequest request,
                           @AuthenticationPrincipal CustomUserDetails principal) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("appName", "Meridian ERP");
        if (principal != null) {
            model.addAttribute("currentUser", principal);
        }
    }
}
