package com.erp.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public String dashboard(Model model) {
        model.addAttribute("data", dashboardService.buildDashboard());
        model.addAttribute("pageTitle", "Dashboard");
        return "dashboard/index";
    }
}
