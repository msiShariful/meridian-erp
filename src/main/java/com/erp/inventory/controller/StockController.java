package com.erp.inventory.controller;

import com.erp.inventory.entity.Product;
import com.erp.inventory.entity.Warehouse;
import com.erp.inventory.enums.MovementType;
import com.erp.inventory.service.ProductService;
import com.erp.inventory.service.StockService;
import com.erp.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/inventory/stock")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('INVENTORY_VIEW')")
public class StockController {

    private final StockService stockService;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("movements", stockService.history(PageRequest.of(page, 20)));
        model.addAttribute("products", productService.all());
        model.addAttribute("warehouses", warehouseService.all());
        model.addAttribute("lowStock", productService.lowStock());
        model.addAttribute("types", MovementType.values());
        model.addAttribute("pageTitle", "Stock Movements");
        return "inventory/stock/index";
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String adjust(@RequestParam UUID productId,
                         @RequestParam(required = false) UUID warehouseId,
                         @RequestParam MovementType type,
                         @RequestParam int quantity,
                         @RequestParam(required = false) String reason,
                         @RequestParam(required = false) String reference,
                         RedirectAttributes ra) {
        Product product = productService.get(productId);
        Warehouse warehouse = warehouseId != null ? warehouseService.get(warehouseId) : null;
        stockService.recordMovement(product, warehouse, type, quantity, reason, reference);
        ra.addFlashAttribute("successMessage", "Stock movement recorded for " + product.getName() + ".");
        return "redirect:/inventory/stock";
    }
}
