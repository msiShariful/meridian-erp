package com.erp.accounting.service;

import com.erp.accounting.entity.Invoice;
import com.erp.accounting.entity.InvoiceItem;
import com.erp.accounting.entity.Payment;
import com.erp.accounting.enums.InvoiceStatus;
import com.erp.accounting.repository.InvoiceRepository;
import com.erp.accounting.repository.PaymentRepository;
import com.erp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Page<Invoice> search(String q, InvoiceStatus status, Pageable pageable) {
        return invoiceRepository.search(blank(q), status, pageable);
    }

    @Transactional(readOnly = true)
    public Invoice get(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Invoice", id));
    }

    @Transactional(readOnly = true)
    public InvoiceForm toForm(Invoice invoice) {
        InvoiceForm form = new InvoiceForm();
        form.setId(invoice.getId());
        form.setInvoiceNumber(invoice.getInvoiceNumber());
        form.setCustomerName(invoice.getCustomerName());
        form.setIssueDate(invoice.getIssueDate());
        form.setDueDate(invoice.getDueDate());
        form.setTaxRate(invoice.getTaxRate());
        form.setDiscount(invoice.getDiscount());
        form.setNotes(invoice.getNotes());
        List<InvoiceForm.Item> items = new ArrayList<>();
        for (InvoiceItem it : invoice.getItems()) {
            InvoiceForm.Item fi = new InvoiceForm.Item();
            fi.setDescription(it.getDescription());
            fi.setQuantity(it.getQuantity());
            fi.setUnitPrice(it.getUnitPrice());
            items.add(fi);
        }
        form.setItems(items);
        return form;
    }

    public Invoice save(InvoiceForm form) {
        Invoice invoice = form.isNew() ? new Invoice() : get(form.getId());
        if (invoice.isNew()) {
            invoice.setInvoiceNumber(nextInvoiceNumber());
            invoice.setStatus(InvoiceStatus.DRAFT);
        }
        invoice.setCustomerName(form.getCustomerName());
        invoice.setIssueDate(form.getIssueDate());
        invoice.setDueDate(form.getDueDate());
        invoice.setTaxRate(form.getTaxRate() != null ? form.getTaxRate() : BigDecimal.ZERO);
        invoice.setDiscount(form.getDiscount() != null ? form.getDiscount() : BigDecimal.ZERO);
        invoice.setNotes(form.getNotes());

        invoice.getItems().clear();
        BigDecimal subtotal = BigDecimal.ZERO;
        if (form.getItems() != null) {
            for (InvoiceForm.Item fi : form.getItems()) {
                if (fi == null || fi.getDescription() == null || fi.getDescription().isBlank()) {
                    continue;
                }
                int qty = fi.getQuantity() > 0 ? fi.getQuantity() : 0;
                BigDecimal unit = fi.getUnitPrice() != null ? fi.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(qty));
                InvoiceItem item = InvoiceItem.builder()
                        .invoice(invoice)
                        .description(fi.getDescription().trim())
                        .quantity(qty)
                        .unitPrice(unit)
                        .lineTotal(lineTotal)
                        .build();
                invoice.getItems().add(item);
                subtotal = subtotal.add(lineTotal);
            }
        }

        BigDecimal taxRate = invoice.getTaxRate();
        BigDecimal taxAmount = subtotal.multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxAmount).subtract(invoice.getDiscount());

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotal(total.max(BigDecimal.ZERO));
        return invoiceRepository.save(invoice);
    }

    /** Records a payment against an invoice and recomputes its status. */
    public void recordPayment(UUID invoiceId, Payment payment) {
        Invoice invoice = get(invoiceId);
        payment.setInvoice(invoice);
        paymentRepository.save(payment);
        refreshStatus(invoice);
    }

    public void refreshStatus(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.DRAFT) {
            // keep drafts as drafts until sent
        }
        BigDecimal paid = paymentRepository.sumByInvoiceId(invoice.getId());
        if (paid == null) {
            paid = BigDecimal.ZERO;
        }
        if (paid.compareTo(invoice.getTotal()) >= 0 && invoice.getTotal().compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIAL);
        }
        invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public BigDecimal amountPaid(UUID invoiceId) {
        BigDecimal paid = paymentRepository.sumByInvoiceId(invoiceId);
        return paid != null ? paid : BigDecimal.ZERO;
    }

    public void delete(UUID id) {
        invoiceRepository.deleteById(id);
    }

    private String nextInvoiceNumber() {
        long count = invoiceRepository.count();
        return String.format("INV-%04d", count + 1);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
