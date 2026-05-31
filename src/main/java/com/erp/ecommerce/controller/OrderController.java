package com.erp.ecommerce.controller;

import com.erp.ecommerce.entity.Order;
import com.erp.ecommerce.entity.OrderItem;
import com.erp.ecommerce.enums.OrderStatus;
import com.erp.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequestMapping("/ecommerce/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ECOMMERCE_VIEW')")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) OrderStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        Page<Order> orders = orderService.search(q, status,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("orders", orders);
        model.addAttribute("q", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("pageTitle", "Orders");
        return "ecommerce/orders/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("pageTitle", "New Order");
        return "ecommerce/orders/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String save(@ModelAttribute("order") Order order,
                       @RequestParam(required = false) String productName,
                       @RequestParam(required = false) Integer quantity,
                       @RequestParam(required = false) BigDecimal unitPrice,
                       RedirectAttributes ra) {
        // Optional single-line manual order entry (storefront is the main source).
        order.getItems().clear();
        BigDecimal subtotal = BigDecimal.ZERO;
        if (productName != null && !productName.isBlank() && quantity != null && unitPrice != null) {
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            order.addItem(OrderItem.builder()
                    .productName(productName).quantity(quantity)
                    .unitPrice(unitPrice).lineTotal(lineTotal).build());
            subtotal = lineTotal;
        }
        order.setSubtotal(subtotal);
        BigDecimal shipping = order.getShipping() != null ? order.getShipping() : BigDecimal.ZERO;
        BigDecimal discount = order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO;
        order.setShipping(shipping);
        order.setDiscount(discount);
        order.setTotal(subtotal.subtract(discount).add(shipping));
        orderService.save(order);
        ra.addFlashAttribute("successMessage", "Order saved successfully.");
        return "redirect:/ecommerce/orders";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Order order = orderService.get(id);
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("pageTitle", order.getOrderNumber());
        return "ecommerce/orders/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/status")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String updateStatus(@PathVariable UUID id, @RequestParam OrderStatus status, RedirectAttributes ra) {
        orderService.updateStatus(id, status);
        ra.addFlashAttribute("successMessage", "Order status updated to " + status.getLabel() + ".");
        return "redirect:/ecommerce/orders/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        orderService.delete(id);
        ra.addFlashAttribute("successMessage", "Order deleted.");
        return "redirect:/ecommerce/orders";
    }
}
