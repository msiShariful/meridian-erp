package com.erp.settings.controller;

import com.erp.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/settings/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("users", userService.search(q, PageRequest.of(page, 20)));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "User Management");
        return "settings/users";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("roles", userService.allRoles());
        model.addAttribute("editUser", null);
        model.addAttribute("pageTitle", "New User");
        return "settings/user-form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("editUser", userService.getById(id));
        model.addAttribute("roles", userService.allRoles());
        model.addAttribute("pageTitle", "Edit User");
        return "settings/user-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) UUID id,
                       @RequestParam String fullName,
                       @RequestParam(required = false) String email,
                       @RequestParam(required = false) String password,
                       @RequestParam(required = false) String jobTitle,
                       @RequestParam(required = false) List<String> roles,
                       @RequestParam(defaultValue = "true") boolean enabled,
                       RedirectAttributes ra) {
        Set<String> roleNames = new HashSet<>(roles == null ? List.of() : roles);
        if (id == null) {
            userService.createUser(fullName, email,
                    (password == null || password.isBlank()) ? "Admin@1234" : password,
                    jobTitle, roleNames);
            ra.addFlashAttribute("successMessage", "User created. Default password is Admin@1234 if none was set.");
        } else {
            userService.updateUser(id, fullName, jobTitle, roleNames, enabled);
            ra.addFlashAttribute("successMessage", "User updated.");
        }
        return "redirect:/settings/users";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/toggle")
    public String toggle(@PathVariable UUID id, @RequestParam boolean enabled, RedirectAttributes ra) {
        userService.setEnabled(id, enabled);
        ra.addFlashAttribute("successMessage", "User " + (enabled ? "activated" : "deactivated") + ".");
        return "redirect:/settings/users";
    }
}
