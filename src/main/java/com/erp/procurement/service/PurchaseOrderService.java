package com.erp.procurement.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.procurement.entity.GRN;
import com.erp.procurement.entity.POItem;
import com.erp.procurement.entity.PurchaseOrder;
import com.erp.procurement.entity.Vendor;
import com.erp.procurement.enums.GRNStatus;
import com.erp.procurement.enums.POStatus;
import com.erp.procurement.repository.GRNRepository;
import com.erp.procurement.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GRNRepository grnRepository;
    private final VendorService vendorService;

    @Transactional(readOnly = true)
    public Page<PurchaseOrder> search(String q, Pageable pageable) {
        return purchaseOrderRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public PurchaseOrder get(UUID id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Purchase Order", id));
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> forVendor(Vendor vendor) {
        return purchaseOrderRepository.findByVendorOrderByOrderDateDesc(vendor);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderForm toForm(PurchaseOrder po) {
        PurchaseOrderForm form = new PurchaseOrderForm();
        form.setId(po.getId());
        form.setPoNumber(po.getPoNumber());
        form.setVendorId(po.getVendor() != null ? po.getVendor().getId() : null);
        form.setOrderDate(po.getOrderDate());
        form.setExpectedDate(po.getExpectedDate());
        form.setNotes(po.getNotes());
        List<PurchaseOrderForm.Item> items = new ArrayList<>();
        for (POItem it : po.getItems()) {
            PurchaseOrderForm.Item fi = new PurchaseOrderForm.Item();
            fi.setDescription(it.getDescription());
            fi.setQuantity(it.getQuantity());
            fi.setUnitPrice(it.getUnitPrice());
            items.add(fi);
        }
        form.setItems(items);
        return form;
    }

    public PurchaseOrder save(PurchaseOrderForm form) {
        PurchaseOrder po = form.isNew() ? new PurchaseOrder() : get(form.getId());
        if (po.isNew()) {
            po.setPoNumber(nextPoNumber());
            po.setStatus(POStatus.DRAFT);
        }
        Vendor vendor = form.getVendorId() != null ? vendorService.get(form.getVendorId()) : null;
        po.setVendor(vendor);
        po.setOrderDate(form.getOrderDate() != null ? form.getOrderDate() : LocalDate.now());
        po.setExpectedDate(form.getExpectedDate());
        po.setNotes(form.getNotes());

        po.getItems().clear();
        BigDecimal subtotal = BigDecimal.ZERO;
        if (form.getItems() != null) {
            for (PurchaseOrderForm.Item fi : form.getItems()) {
                if (fi == null || fi.getDescription() == null || fi.getDescription().isBlank()) {
                    continue;
                }
                int qty = Math.max(fi.getQuantity(), 0);
                BigDecimal unit = fi.getUnitPrice() != null ? fi.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(qty));
                POItem item = POItem.builder()
                        .purchaseOrder(po)
                        .description(fi.getDescription().trim())
                        .quantity(qty)
                        .unitPrice(unit)
                        .lineTotal(lineTotal)
                        .build();
                po.getItems().add(item);
                subtotal = subtotal.add(lineTotal);
            }
        }
        po.setSubtotal(subtotal);
        po.setTotal(subtotal);
        return purchaseOrderRepository.save(po);
    }

    public void send(UUID id) {
        PurchaseOrder po = get(id);
        po.setStatus(POStatus.SENT);
        purchaseOrderRepository.save(po);
    }

    public void close(UUID id) {
        PurchaseOrder po = get(id);
        po.setStatus(POStatus.CLOSED);
        purchaseOrderRepository.save(po);
    }

    /** Receives a PO: creates a GRN and updates the PO status accordingly. */
    public GRN receive(UUID id, String receivedBy, boolean partial, String notes) {
        PurchaseOrder po = get(id);
        GRNStatus grnStatus = partial ? GRNStatus.PARTIAL : GRNStatus.COMPLETE;
        GRN grn = GRN.builder()
                .grnNumber(nextGrnNumber())
                .purchaseOrder(po)
                .receivedDate(LocalDate.now())
                .receivedBy(receivedBy != null && !receivedBy.isBlank() ? receivedBy : "Warehouse")
                .notes(notes)
                .status(grnStatus)
                .build();
        grnRepository.save(grn);
        po.setStatus(partial ? POStatus.PARTIAL : POStatus.RECEIVED);
        purchaseOrderRepository.save(po);
        return grn;
    }

    public void delete(UUID id) {
        purchaseOrderRepository.deleteById(id);
    }

    private String nextPoNumber() {
        long count = purchaseOrderRepository.count();
        return String.format("PO-%04d", count + 1);
    }

    private String nextGrnNumber() {
        long count = grnRepository.count();
        return String.format("GRN-%04d", count + 1);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
