package com.erp.ecommerce.controller;

import com.erp.ecommerce.entity.Coupon;
import com.erp.ecommerce.enums.CouponType;
import com.erp.ecommerce.service.CouponService;
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
@RequestMapping("/ecommerce/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ECOMMERCE_VIEW')")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("coupons", couponService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Coupons");
        return "ecommerce/coupons/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        model.addAttribute("types", CouponType.values());
        model.addAttribute("pageTitle", "New Coupon");
        return "ecommerce/coupons/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("coupon", couponService.get(id));
        model.addAttribute("types", CouponType.values());
        model.addAttribute("pageTitle", "Edit Coupon");
        return "ecommerce/coupons/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String save(@ModelAttribute("coupon") Coupon coupon, RedirectAttributes ra) {
        if (coupon.getCode() != null) coupon.setCode(coupon.getCode().trim().toUpperCase());
        couponService.save(coupon);
        ra.addFlashAttribute("successMessage", "Coupon saved successfully.");
        return "redirect:/ecommerce/coupons";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        couponService.delete(id);
        ra.addFlashAttribute("successMessage", "Coupon deleted.");
        return "redirect:/ecommerce/coupons";
    }
}
