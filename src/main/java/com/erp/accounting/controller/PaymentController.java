package com.erp.accounting.controller;

import com.erp.accounting.entity.Invoice;
import com.erp.accounting.entity.Payment;
import com.erp.accounting.enums.PaymentMethod;
import com.erp.accounting.service.InvoiceService;
import com.erp.accounting.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/accounting/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ACCOUNTING_VIEW')")
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("payments", paymentService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "date"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Payments");
        return "accounting/payments/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String createForm(Model model) {
        Payment payment = new Payment();
        payment.setDate(LocalDate.now());
        model.addAttribute("payment", payment);
        prepareForm(model);
        model.addAttribute("pageTitle", "Record Payment");
        return "accounting/payments/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        Payment payment = paymentService.get(id);
        model.addAttribute("payment", payment);
        model.addAttribute("selectedInvoiceId", payment.getInvoice() != null ? payment.getInvoice().getId() : null);
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Payment");
        return "accounting/payments/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String save(@ModelAttribute("payment") Payment payment,
                       @RequestParam(value = "invoiceId", required = false) UUID invoiceId,
                       RedirectAttributes ra) {
        Invoice invoice = invoiceId != null ? invoiceService.get(invoiceId) : null;
        payment.setInvoice(invoice);
        paymentService.save(payment);
        if (invoice != null) {
            invoiceService.refreshStatus(invoice);
        }
        ra.addFlashAttribute("successMessage", "Payment saved.");
        return "redirect:/accounting/payments";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ACCOUNTING_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        paymentService.delete(id);
        ra.addFlashAttribute("successMessage", "Payment deleted.");
        return "redirect:/accounting/payments";
    }

    private void prepareForm(Model model) {
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("invoices", invoiceService.search(null, null,
                PageRequest.of(0, 200, Sort.by(Sort.Direction.DESC, "issueDate"))).getContent());
    }
}
