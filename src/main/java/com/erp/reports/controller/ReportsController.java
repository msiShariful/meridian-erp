package com.erp.reports.controller;

import com.erp.reports.service.ReportExportService;
import com.erp.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('REPORTS_VIEW')")
public class ReportsController {

    private static final List<String> MODULES = List.of(
            "leads", "deals", "invoices", "orders", "tickets", "expenses", "employees");

    private final ReportService reportService;
    private final ReportExportService reportExportService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("data", reportService.buildAnalytics());
        model.addAttribute("pageTitle", "Reports & Analytics");
        return "reports/index";
    }

    @GetMapping("/custom")
    public String custom(@RequestParam(required = false) String module,
                         @RequestParam(required = false) String from,
                         @RequestParam(required = false) String to,
                         @RequestParam(required = false) String groupBy,
                         Model model) {
        model.addAttribute("report", reportService.buildCustomReport(module, from, to, groupBy));
        model.addAttribute("modules", MODULES);
        model.addAttribute("pageTitle", "Custom Report");
        return "reports/custom";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam(required = false) String type) {
        byte[] body = reportExportService.buildExcel(type);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("report.xlsx").build());
        return ResponseEntity.ok().headers(headers).body(body);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(required = false) String type) {
        byte[] body = reportExportService.buildPdf(type);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("report.pdf").build());
        return ResponseEntity.ok().headers(headers).body(body);
    }
}
