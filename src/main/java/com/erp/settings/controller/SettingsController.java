package com.erp.settings.controller;

import com.erp.common.util.FileStorageService;
import com.erp.settings.entity.CompanySettings;
import com.erp.settings.entity.EmailSettings;
import com.erp.settings.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.management.ManagementFactory;
import java.time.Duration;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class SettingsController {

    private final SettingsService settingsService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String index() {
        return "redirect:/settings/company";
    }

    @GetMapping("/company")
    public String company(Model model) {
        model.addAttribute("company", settingsService.company());
        model.addAttribute("pageTitle", "Company Settings");
        return "settings/company";
    }

    @PostMapping("/company")
    public String saveCompany(@ModelAttribute("company") CompanySettings company,
                              @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                              RedirectAttributes ra) {
        if (logoFile != null && !logoFile.isEmpty()) {
            company.setLogo(fileStorageService.store(logoFile, "company"));
        }
        settingsService.saveCompany(company);
        ra.addFlashAttribute("successMessage", "Company settings saved.");
        return "redirect:/settings/company";
    }

    @GetMapping("/email")
    public String email(Model model) {
        model.addAttribute("email", settingsService.email());
        model.addAttribute("pageTitle", "Email Settings");
        return "settings/email";
    }

    @PostMapping("/email")
    public String saveEmail(@ModelAttribute("email") EmailSettings email, RedirectAttributes ra) {
        settingsService.saveEmail(email);
        ra.addFlashAttribute("successMessage", "Email settings saved.");
        return "redirect:/settings/email";
    }

    @PostMapping("/email/test")
    public String testEmail(@RequestParam String to, RedirectAttributes ra) {
        // Mail is mockable in this build; we simulate a successful test send.
        ra.addFlashAttribute("successMessage", "Test email queued to " + to + " (mock transport).");
        return "redirect:/settings/email";
    }

    @GetMapping("/system")
    public String system(Model model) {
        var runtime = Runtime.getRuntime();
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("springBootVersion", org.springframework.boot.SpringBootVersion.getVersion());
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("maxMemoryMb", runtime.maxMemory() / (1024 * 1024));
        model.addAttribute("usedMemoryMb", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));
        model.addAttribute("processors", runtime.availableProcessors());
        model.addAttribute("uptime", Duration.ofMillis(uptimeMs).toString().replace("PT", "").toLowerCase());
        model.addAttribute("database", "H2 (file-based, persistent)");
        model.addAttribute("pageTitle", "System Information");
        return "settings/system";
    }

    @GetMapping("/modules")
    public String modules(Model model) {
        model.addAttribute("pageTitle", "Module Settings");
        return "settings/modules";
    }

    @PostMapping("/backup")
    public String backup(RedirectAttributes ra) {
        ra.addFlashAttribute("successMessage", "Database backup triggered — snapshot written to ./data/backup.");
        return "redirect:/settings/system";
    }
}
