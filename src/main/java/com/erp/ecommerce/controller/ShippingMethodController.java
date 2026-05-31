package com.erp.ecommerce.controller;

import com.erp.ecommerce.entity.ShippingMethod;
import com.erp.ecommerce.service.ShippingMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/ecommerce/shipping")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ECOMMERCE_VIEW')")
public class ShippingMethodController {

    private final ShippingMethodService shippingMethodService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("methods", shippingMethodService.all());
        model.addAttribute("pageTitle", "Shipping Methods");
        return "ecommerce/shipping/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("method", new ShippingMethod());
        model.addAttribute("pageTitle", "New Shipping Method");
        return "ecommerce/shipping/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("method", shippingMethodService.get(id));
        model.addAttribute("pageTitle", "Edit Shipping Method");
        return "ecommerce/shipping/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String save(@ModelAttribute("method") ShippingMethod method, RedirectAttributes ra) {
        shippingMethodService.save(method);
        ra.addFlashAttribute("successMessage", "Shipping method saved successfully.");
        return "redirect:/ecommerce/shipping";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        shippingMethodService.delete(id);
        ra.addFlashAttribute("successMessage", "Shipping method deleted.");
        return "redirect:/ecommerce/shipping";
    }
}
