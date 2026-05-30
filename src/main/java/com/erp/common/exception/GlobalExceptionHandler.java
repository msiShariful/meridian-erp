package com.erp.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Centralised exception handling. Business errors become friendly flash messages that
 * redirect back; not-found and access-denied render their dedicated pages; anything
 * else falls through to the generic 500 page.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex, RedirectAttributes ra, HttpServletRequest request) {
        log.warn("Business rule violation: {}", ex.getMessage());
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("statusCode", 404);
        model.addAttribute("statusText", "Not Found");
        model.addAttribute("title", "Not Found");
        model.addAttribute("message", ex.getMessage());
        return "errors/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(Model model) {
        model.addAttribute("statusCode", 403);
        model.addAttribute("statusText", "Forbidden");
        model.addAttribute("title", "Access Denied");
        model.addAttribute("message", "You don't have permission to view this page.");
        return "errors/403";
    }
}
