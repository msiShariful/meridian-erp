package com.erp.core.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int code = status != null ? Integer.parseInt(status.toString()) : 500;
        return render(code, model);
    }

    @GetMapping("/error/403")
    public String forbidden(Model model) {
        return render(403, model);
    }

    private String render(int code, Model model) {
        HttpStatus httpStatus = HttpStatus.resolve(code);
        model.addAttribute("statusCode", code);
        model.addAttribute("statusText", httpStatus != null ? httpStatus.getReasonPhrase() : "Error");
        switch (code) {
            case 403 -> {
                model.addAttribute("title", "Access Denied");
                model.addAttribute("message", "You don't have permission to view this page.");
                return "errors/403";
            }
            case 404 -> {
                model.addAttribute("title", "Page Not Found");
                model.addAttribute("message", "The page you're looking for doesn't exist or has moved.");
                return "errors/404";
            }
            default -> {
                model.addAttribute("title", "Something Went Wrong");
                model.addAttribute("message", "An unexpected error occurred. Our team has been notified.");
                return "errors/500";
            }
        }
    }
}
