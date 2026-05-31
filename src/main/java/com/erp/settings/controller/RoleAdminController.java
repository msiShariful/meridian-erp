package com.erp.settings.controller;

import com.erp.settings.service.RoleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/settings/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class RoleAdminController {

    private final RoleAdminService roleAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("roles", roleAdminService.allRoles());
        model.addAttribute("pageTitle", "Role Management");
        return "settings/roles";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    public String editMatrix(@PathVariable UUID id, Model model) {
        model.addAttribute("role", roleAdminService.getRole(id));
        model.addAttribute("permissions", roleAdminService.allPermissions());
        model.addAttribute("pageTitle", "Edit Role Permissions");
        return "settings/role-form";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/permissions")
    public String updatePermissions(@PathVariable UUID id,
                                    @RequestParam(required = false) List<String> permissions,
                                    RedirectAttributes ra) {
        roleAdminService.updatePermissions(id, permissions);
        ra.addFlashAttribute("successMessage", "Role permissions updated.");
        return "redirect:/settings/roles";
    }

    @PostMapping("/save")
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String displayName,
                         @RequestParam(required = false) String description,
                         RedirectAttributes ra) {
        roleAdminService.createRole(name, displayName, description);
        ra.addFlashAttribute("successMessage", "Role created.");
        return "redirect:/settings/roles";
    }
}
