package com.erp.core.controller;

import com.erp.core.service.CustomUserDetails;
import com.erp.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String profile(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("profile", userService.getByEmail(principal.getUsername()));
        model.addAttribute("pageTitle", "My Profile");
        return "core/profile";
    }

    @PostMapping
    public String update(@AuthenticationPrincipal CustomUserDetails principal,
                         @RequestParam String fullName,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) String jobTitle,
                         RedirectAttributes ra) {
        userService.updateProfile(principal.getUsername(), fullName, phone, jobTitle);
        ra.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("errorMessage", "New password and confirmation do not match.");
            return "redirect:/profile";
        }
        userService.changePassword(principal.getUsername(), currentPassword, newPassword);
        ra.addFlashAttribute("successMessage", "Password changed successfully.");
        return "redirect:/profile";
    }

    @PostMapping("/avatar")
    public String uploadAvatar(@AuthenticationPrincipal CustomUserDetails principal,
                               @RequestParam("avatar") MultipartFile avatar,
                               RedirectAttributes ra) {
        userService.updateAvatar(principal.getUsername(), avatar);
        ra.addFlashAttribute("successMessage", "Avatar updated.");
        return "redirect:/profile";
    }
}
