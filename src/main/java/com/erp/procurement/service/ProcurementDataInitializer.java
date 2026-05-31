package com.erp.procurement.service;

import com.erp.procurement.entity.GRN;
import com.erp.procurement.entity.POItem;
import com.erp.procurement.entity.PurchaseOrder;
import com.erp.procurement.entity.Requisition;
import com.erp.procurement.entity.Vendor;
import com.erp.procurement.entity.VendorBill;
import com.erp.procurement.enums.BillStatus;
import com.erp.procurement.enums.GRNStatus;
import com.erp.procurement.enums.POStatus;
import com.erp.procurement.enums.RequisitionStatus;
import com.erp.procurement.enums.VendorStatus;
import com.erp.procurement.repository.GRNRepository;
import com.erp.procurement.repository.PurchaseOrderRepository;
import com.erp.procurement.repository.RequisitionRepository;
import com.erp.procurement.repository.VendorBillRepository;
import com.erp.procurement.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds representative procurement data (vendors, requisitions, purchase orders
 * with line items, GRNs and vendor bills) on first startup.
 */
@Slf4j
@Component
@Order(8)
@RequiredArgsConstructor
public class ProcurementDataInitializer {

    private final VendorRepository vendorRepository;
    private final RequisitionRepository requisitionRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GRNRepository grnRepository;
    private final VendorBillRepository vendorBillRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(8)
    @Transactional
    public void seed() {
        if (vendorRepository.count() > 0) {
            return;
        }
        log.info("Seeding Procurement demo data...");

        // Vendors: name, contactPerson, paymentTerms
        String[][] vendorData = {
                {"Akij Enterprise Ltd", "Mizanur Rahman", "Net 30"},
                {"Pran Foods Ltd", "Sabina Yasmin", "Net 45"},
                {"Bashundhara Paper Mills", "Kamrul Hasan", "Net 15"},
                {"BSRM Steels Ltd", "Rafiqul Islam", "Net 60"},
                {"Aman Cement Mills", "Nasrin Sultana", "Net 30"},
                {"Meghna Group of Industries", "Shamsul Alam", "Net 30"},
                {"Abul Khair Group", "Farzana Akter", "Advance"},
                {"Navana Furniture", "Habibur Rahman", "Net 30"},
                {"Transcom Electronics", "Tahmina Begum", "Net 45"},
                {"Singer Bangladesh Ltd", "Jahangir Alam", "Net 30"}
        };
        VendorStatus[] vStatuses = VendorStatus.values();
        List<Vendor> vendors = new ArrayList<>();
        for (int i = 0; i < vendorData.length; i++) {
            String[] v = vendorData[i];
            vendors.add(vendorRepository.save(Vendor.builder()
                    .name(v[0]).contactPerson(v[1])
                    .email("procurement@" + v[0].toLowerCase().replaceAll("[^a-z]", "") + ".com.bd")
                    .phone("+8801" + (711000000L + i * 11111111L))
                    .address("Tejgaon Industrial Area, Dhaka 1208")
                    .paymentTerms(v[2])
                    .rating((i % 5) + 1)
                    .status(vStatuses[i % vStatuses.length])
                    .build()));
        }

        // Requisitions across statuses
        String[][] reqData = {
                {"IT", "Tariq Aziz", "Replace ageing developer laptops"},
                {"Operations", "Rashed Khan", "Annual stationery restock"},
                {"Facilities", "Lamia Chowdhury", "Office air-conditioning units"},
                {"Production", "Mahbub Alam", "Raw material - steel rods"},
                {"Marketing", "Shabnam Ferdousi", "Exhibition booth materials"},
                {"HR", "Tanjina Akhter", "Employee welcome kits"},
                {"Finance", "Rumana Islam", "Accounting software licenses"},
                {"IT", "Sabbir Ahmed", "Network switches and cabling"}
        };
        RequisitionStatus[] reqStatuses = RequisitionStatus.values();
        for (int i = 0; i < reqData.length; i++) {
            String[] r = reqData[i];
            requisitionRepository.save(Requisition.builder()
                    .reqNumber(String.format("REQ-%04d", i + 1))
                    .department(r[0]).requestedBy(r[1]).justification(r[2])
                    .status(reqStatuses[i % reqStatuses.length])
                    .estimatedCost(BigDecimal.valueOf((i + 1) * 85000L))
                    .items("See attached specification sheet.")
                    .build());
        }

        // Purchase orders with line items, across statuses
        String[][] poItemData = {
                {"Dell Latitude 5440 Laptop", "Cisco Catalyst Switch", "UPS Battery Backup"},
                {"A4 Photocopy Paper (Ream)", "Ballpoint Pens (Box)", "File Folders (Pack)"},
                {"Split AC 1.5 Ton", "AC Installation Service", ""},
                {"MS Steel Rod 12mm (Ton)", "Binding Wire (kg)", ""},
                {"Pull-up Banner Stand", "Brochure Printing (1000)", "Promotional Mugs"},
                {"Welcome Kit Bag", "Branded Notebook", "Stainless Water Bottle"},
                {"ERP License (Annual)", "Premium Support Plan", ""},
                {"Office Desk", "Ergonomic Chair", "Filing Cabinet"},
                {"LED Monitor 24inch", "Wireless Keyboard & Mouse", ""},
                {"Cement Bag (50kg)", "Sand (CFT)", "Brick (1000pcs)"}
        };
        POStatus[] poStatuses = POStatus.values();
        List<PurchaseOrder> orders = new ArrayList<>();
        for (int i = 0; i < poItemData.length; i++) {
            Vendor vendor = vendors.get(i % vendors.size());
            POStatus status = poStatuses[i % poStatuses.length];
            PurchaseOrder po = PurchaseOrder.builder()
                    .poNumber(String.format("PO-%04d", i + 1))
                    .vendor(vendor)
                    .status(status)
                    .orderDate(LocalDate.now().minusDays(30L - i))
                    .expectedDate(LocalDate.now().plusDays(7L + i))
                    .notes("Delivery to central warehouse, Gulshan.")
                    .build();

            BigDecimal subtotal = BigDecimal.ZERO;
            int line = 0;
            for (String desc : poItemData[i]) {
                if (desc == null || desc.isBlank()) continue;
                int qty = (line + 1) * 2;
                BigDecimal unit = BigDecimal.valueOf(2500L + (long) (i + 1) * (line + 1) * 1500L);
                BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(qty));
                po.getItems().add(POItem.builder()
                        .purchaseOrder(po)
                        .description(desc).quantity(qty).unitPrice(unit).lineTotal(lineTotal)
                        .build());
                subtotal = subtotal.add(lineTotal);
                line++;
            }
            po.setSubtotal(subtotal);
            po.setTotal(subtotal);
            orders.add(purchaseOrderRepository.save(po));
        }

        // GRNs against received / partial POs
        int grnCount = 0;
        for (PurchaseOrder po : orders) {
            if (po.getStatus() == POStatus.RECEIVED || po.getStatus() == POStatus.PARTIAL
                    || po.getStatus() == POStatus.CLOSED) {
                grnCount++;
                grnRepository.save(GRN.builder()
                        .grnNumber(String.format("GRN-%04d", grnCount))
                        .purchaseOrder(po)
                        .receivedDate(po.getOrderDate().plusDays(5))
                        .receivedBy("Warehouse Team")
                        .notes("Goods inspected and accepted.")
                        .status(po.getStatus() == POStatus.PARTIAL ? GRNStatus.PARTIAL : GRNStatus.COMPLETE)
                        .build());
            }
        }

        // Vendor bills
        int billCount = 0;
        for (int i = 0; i < orders.size() && billCount < 5; i++) {
            PurchaseOrder po = orders.get(i);
            if (po.getStatus() == POStatus.RECEIVED || po.getStatus() == POStatus.CLOSED) {
                billCount++;
                vendorBillRepository.save(VendorBill.builder()
                        .billNumber(String.format("BILL-%04d", billCount))
                        .vendor(po.getVendor())
                        .purchaseOrder(po)
                        .amount(po.getTotal())
                        .dueDate(LocalDate.now().plusDays(20L + i))
                        .status(i % 2 == 0 ? BillStatus.PENDING : BillStatus.PAID)
                        .build());
            }
        }

        log.info("Procurement demo data seeded: {} vendors, {} requisitions, {} POs, {} GRNs, {} bills.",
                vendors.size(), reqData.length, orders.size(), grnCount, billCount);
    }
}
