package com.erp.accounting.controller;

import com.erp.accounting.entity.Invoice;
import com.erp.accounting.entity.Payment;
import com.erp.accounting.enums.InvoiceStatus;
import com.erp.accounting.enums.PaymentMethod;
import com.erp.accounting.service.InvoiceForm;
import com.erp.accounting.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/accounting/invoices")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ACCOUNTING_VIEW')")
public class InvoiceController {

    private static final int ITEM_ROWS = 5;

    private final InvoiceService invoiceService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) InvoiceStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("invoices", invoiceService.search(q, status,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "issueDate"))));
        model.addAttribute("q", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", InvoiceStatus.values());
        model.addAttribute("pageTitle", "Invoices");
        return "accounting/invoices/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String createForm(Model model) {
        InvoiceForm form = new InvoiceForm();
        padItems(form);
        model.addAttribute("invoiceForm", form);
        model.addAttribute("pageTitle", "New Invoice");
        return "accounting/invoices/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        InvoiceForm form = invoiceService.toForm(invoiceService.get(id));
        padItems(form);
        model.addAttribute("invoiceForm", form);
        model.addAttribute("pageTitle", "Edit Invoice");
        return "accounting/invoices/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String save(@ModelAttribute("invoiceForm") InvoiceForm form, RedirectAttributes ra) {
        Invoice saved = invoiceService.save(form);
        ra.addFlashAttribute("successMessage", "Invoice " + saved.getInvoiceNumber() + " saved.");
        return "redirect:/accounting/invoices/" + saved.getId();
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Invoice invoice = invoiceService.get(id);
        BigDecimal paid = invoiceService.amountPaid(id);
        model.addAttribute("invoice", invoice);
        model.addAttribute("amountPaid", paid);
        model.addAttribute("amountDue", invoice.getTotal().subtract(paid));
        model.addAttribute("payment", new Payment());
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("pageTitle", invoice.getInvoiceNumber());
        return "accounting/invoices/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/payments")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String recordPayment(@PathVariable UUID id, @ModelAttribute Payment payment,
                                RedirectAttributes ra) {
        invoiceService.recordPayment(id, payment);
        ra.addFlashAttribute("successMessage", "Payment recorded.");
        return "redirect:/accounting/invoices/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        invoiceService.delete(id);
        ra.addFlashAttribute("successMessage", "Invoice deleted.");
        return "redirect:/accounting/invoices";
    }

    private void padItems(InvoiceForm form) {
        while (form.getItems().size() < ITEM_ROWS) {
            form.getItems().add(new InvoiceForm.Item());
        }
    }
}
