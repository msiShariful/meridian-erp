package com.erp.inventory.service;

import com.erp.inventory.entity.Product;
import com.erp.inventory.entity.ProductCategory;
import com.erp.inventory.entity.StockMovement;
import com.erp.inventory.entity.Supplier;
import com.erp.inventory.entity.Warehouse;
import com.erp.inventory.enums.MovementType;
import com.erp.inventory.repository.ProductCategoryRepository;
import com.erp.inventory.repository.ProductRepository;
import com.erp.inventory.repository.StockMovementRepository;
import com.erp.inventory.repository.SupplierRepository;
import com.erp.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds representative Inventory data (categories, warehouses, products, suppliers
 * and stock movements) on first startup so the module is immediately explorable.
 */
@Slf4j
@Component
@Order(4)
@RequiredArgsConstructor
public class InventoryDataInitializer {

    private final ProductCategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockMovementRepository movementRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(4)
    @Transactional
    public void seed() {
        if (productRepository.count() > 0) {
            return;
        }
        log.info("Seeding Inventory demo data...");

        String[][] categoryData = {
                {"Electronics", "Consumer electronics and gadgets"},
                {"Apparel", "Clothing and fashion items"},
                {"Groceries", "Food and daily essentials"},
                {"Home & Living", "Furniture and household goods"},
                {"Stationery", "Office and school supplies"},
                {"Hardware", "Tools and building materials"}
        };
        List<ProductCategory> categories = new ArrayList<>();
        for (String[] c : categoryData) {
            categories.add(categoryRepository.save(ProductCategory.builder()
                    .name(c[0]).description(c[1]).build()));
        }

        String[][] warehouseData = {
                {"Dhaka Main", "Tejgaon Industrial Area, Dhaka", "Imran Hossain", "50000"},
                {"Chattogram", "Agrabad C/A, Chattogram", "Sumon Das", "30000"},
                {"Sylhet", "Amberkhana, Sylhet", "Mizanur Rahman", "15000"}
        };
        List<Warehouse> warehouses = new ArrayList<>();
        for (String[] w : warehouseData) {
            warehouses.add(warehouseRepository.save(Warehouse.builder()
                    .name(w[0]).location(w[1]).manager(w[2]).capacity(Integer.parseInt(w[3])).build()));
        }

        // product: name, categoryIndex, cost, selling, qty, reorder, unit
        Object[][] productData = {
                {"Samsung Galaxy A55", 0, 42000, 48000, 35, 10, "pcs"},
                {"Walton Primo R10", 0, 15000, 18500, 60, 15, "pcs"},
                {"LED Smart TV 43\"", 0, 32000, 39000, 12, 5, "pcs"},
                {"Wireless Earbuds Pro", 0, 1800, 2900, 0, 20, "pcs"},
                {"USB-C Fast Charger", 0, 450, 850, 8, 25, "pcs"},
                {"Bluetooth Speaker", 0, 1200, 2200, 40, 10, "pcs"},
                {"Men's Cotton Polo Shirt", 1, 380, 750, 150, 30, "pcs"},
                {"Women's Denim Jeans", 1, 620, 1250, 90, 25, "pcs"},
                {"Kids T-Shirt Pack", 1, 250, 520, 4, 20, "pack"},
                {"Winter Hoodie", 1, 700, 1400, 55, 15, "pcs"},
                {"Leather Belt", 1, 180, 420, 0, 10, "pcs"},
                {"Basmati Rice 5kg", 2, 620, 780, 200, 50, "bag"},
                {"Soybean Oil 5L", 2, 780, 920, 120, 40, "bottle"},
                {"Red Lentil 1kg", 2, 110, 145, 18, 30, "kg"},
                {"Sugar 1kg", 2, 95, 120, 300, 60, "kg"},
                {"Powder Milk 1kg", 2, 580, 720, 6, 20, "pack"},
                {"Office Desk Chair", 3, 4500, 7200, 22, 8, "pcs"},
                {"LED Desk Lamp", 3, 650, 1200, 45, 12, "pcs"},
                {"Storage Cabinet", 3, 6800, 9500, 9, 5, "pcs"},
                {"Cotton Bed Sheet Set", 3, 850, 1600, 0, 15, "set"},
                {"Wall Clock", 3, 320, 650, 70, 20, "pcs"},
                {"A4 Paper Ream", 4, 280, 360, 250, 50, "ream"},
                {"Ballpoint Pen Box", 4, 90, 160, 14, 40, "box"},
                {"Spiral Notebook", 4, 45, 90, 400, 80, "pcs"},
                {"Stapler Heavy Duty", 4, 180, 340, 30, 10, "pcs"},
                {"Whiteboard Marker Set", 4, 120, 240, 2, 25, "set"},
                {"Cordless Drill", 5, 3200, 4800, 16, 6, "pcs"},
                {"Screwdriver Set", 5, 480, 850, 38, 12, "set"},
                {"Measuring Tape 5m", 5, 95, 190, 60, 20, "pcs"},
                {"Safety Helmet", 5, 220, 420, 0, 15, "pcs"},
                {"Hammer Claw 16oz", 5, 260, 480, 25, 10, "pcs"}
        };

        List<Product> products = new ArrayList<>();
        int seq = 1;
        for (Object[] p : productData) {
            String name = (String) p[0];
            ProductCategory category = categories.get((int) p[1]);
            Product product = productRepository.save(Product.builder()
                    .sku(String.format("SKU-%04d", seq++))
                    .name(name)
                    .category(category)
                    .costPrice(BigDecimal.valueOf(((Number) p[2]).longValue()))
                    .sellingPrice(BigDecimal.valueOf(((Number) p[3]).longValue()))
                    .stockQuantity((int) p[4])
                    .reorderLevel((int) p[5])
                    .unit((String) p[6])
                    .barcode("8800" + String.format("%09d", seq * 7919L % 1000000000L))
                    .description(name + " — quality stock item available at Meridian.")
                    .build());
            products.add(product);
        }

        String[][] supplierData = {
                {"Tech Distributors Ltd", "Rafiqul Islam", "sales@techdist.com.bd", "+8801711000001", "Net 30", "5"},
                {"Fashion Mart BD", "Shahana Begum", "info@fashionmart.bd", "+8801711000002", "Net 15", "4"},
                {"AgroFoods Wholesale", "Jamal Uddin", "orders@agrofoods.bd", "+8801711000003", "Net 45", "4"},
                {"HomeStyle Imports", "Nasreen Akhter", "contact@homestyle.bd", "+8801711000004", "Net 30", "3"},
                {"OfficePro Supplies", "Habibur Rahman", "sales@officepro.bd", "+8801711000005", "Cash on Delivery", "5"},
                {"BuildMart Hardware", "Sohel Rana", "info@buildmart.bd", "+8801711000006", "Net 30", "3"},
                {"Global Electronics Co", "Faisal Ahmed", "trade@globalelec.com", "+8801711000007", "Net 60", "4"},
                {"Daily Essentials Hub", "Ruma Khatun", "supply@dailyhub.bd", "+8801711000008", "Net 15", "2"}
        };
        for (String[] s : supplierData) {
            supplierRepository.save(Supplier.builder()
                    .name(s[0]).contactPerson(s[1]).email(s[2]).phone(s[3])
                    .address("Dhaka, Bangladesh").paymentTerms(s[4]).rating(Integer.parseInt(s[5]))
                    .build());
        }

        MovementType[] types = MovementType.values();
        String[] reasons = {"Initial stock receipt", "Customer order fulfilment", "Stocktake adjustment",
                "Inter-warehouse transfer", "Supplier delivery", "Damaged goods write-off",
                "Promotional dispatch", "Returned items restocked"};
        for (int i = 0; i < 15; i++) {
            Product product = products.get(i % products.size());
            Warehouse warehouse = (i % 4 == 0) ? null : warehouses.get(i % warehouses.size());
            MovementType type = types[i % types.length];
            int qty = ((i % 5) + 1) * 10;
            movementRepository.save(StockMovement.builder()
                    .product(product)
                    .warehouse(warehouse)
                    .type(type)
                    .quantity(qty)
                    .reason(reasons[i % reasons.length])
                    .reference("REF-" + String.format("%05d", 1000 + i))
                    .build());
        }

        log.info("Inventory demo data seeded: {} categories, {} warehouses, {} products, {} suppliers, 15 movements.",
                categories.size(), warehouses.size(), products.size(), supplierData.length);
    }
}
