package com.erp.audit.controller;

import com.erp.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/settings/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String action,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("logs", auditService.search(q, action, PageRequest.of(page, 25)));
        model.addAttribute("q", q);
        model.addAttribute("actionFilter", action);
        model.addAttribute("pageTitle", "Audit Log");
        return "settings/audit";
    }
}
