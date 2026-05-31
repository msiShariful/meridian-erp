package com.erp.ecommerce.controller;

import com.erp.ecommerce.entity.Coupon;
import com.erp.ecommerce.entity.Customer;
import com.erp.ecommerce.entity.Order;
import com.erp.ecommerce.entity.OrderItem;
import com.erp.ecommerce.entity.ShippingMethod;
import com.erp.ecommerce.enums.OrderStatus;
import com.erp.ecommerce.repository.CustomerRepository;
import com.erp.ecommerce.service.Cart;
import com.erp.ecommerce.service.CartLine;
import com.erp.ecommerce.service.CouponService;
import com.erp.ecommerce.service.OrderService;
import com.erp.ecommerce.service.ReviewService;
import com.erp.ecommerce.service.ShippingMethodService;
import com.erp.inventory.entity.Product;
import com.erp.inventory.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private static final String CART_KEY = "ecommerceCart";

    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final CouponService couponService;
    private final ShippingMethodService shippingMethodService;
    private final ReviewService reviewService;
    private final CustomerRepository customerRepository;

    private Cart cart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    // ---------------------------------------------------------------- Catalog

    @GetMapping
    public String catalog(@RequestParam(required = false) String q, Model model, HttpSession session) {
        List<Product> products = productRepository
                .search((q == null || q.isBlank()) ? null : q.trim(),
                        PageRequest.of(0, 60, Sort.by(Sort.Direction.ASC, "name")))
                .getContent();
        model.addAttribute("products", products);
        model.addAttribute("q", q);
        model.addAttribute("cartCount", cart(session).getTotalQuantity());
        return "shop/catalog";
    }

    @GetMapping("/product/{id:[0-9a-fA-F-]{36}}")
    public String product(@PathVariable UUID id, Model model, HttpSession session) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> com.erp.common.exception.ResourceNotFoundException.of("Product", id));
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.approvedFor(product.getName()));
        model.addAttribute("cartCount", cart(session).getTotalQuantity());
        return "shop/product";
    }

    // ------------------------------------------------------------------- Cart

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam UUID productId,
                            @RequestParam(defaultValue = "1") int qty,
                            HttpSession session) {
        cart(session).add(productId, qty);
        return "redirect:/shop/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam UUID productId,
                             @RequestParam(defaultValue = "1") int qty,
                             HttpSession session) {
        cart(session).update(productId, qty);
        return "redirect:/shop/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam UUID productId, HttpSession session) {
        cart(session).remove(productId);
        return "redirect:/shop/cart";
    }

    @PostMapping("/cart/coupon")
    public String applyCoupon(@RequestParam(required = false) String code,
                              HttpSession session, RedirectAttributes ra) {
        Cart cart = cart(session);
        Optional<Coupon> coupon = couponService.findUsable(code);
        if (coupon.isPresent()) {
            cart.setCouponCode(coupon.get().getCode());
            ra.addFlashAttribute("couponMessage", "Coupon \"" + coupon.get().getCode() + "\" applied.");
        } else {
            cart.setCouponCode(null);
            ra.addFlashAttribute("couponError", "Invalid or expired coupon code.");
        }
        return "redirect:/shop/cart";
    }

    @GetMapping("/cart")
    public String cartPage(Model model, HttpSession session) {
        Cart cart = cart(session);
        CartView view = buildCartView(cart);
        model.addAttribute("lines", view.lines);
        model.addAttribute("subtotal", view.subtotal);
        model.addAttribute("discount", view.discount);
        model.addAttribute("couponCode", cart.getCouponCode());
        model.addAttribute("total", view.subtotal.subtract(view.discount));
        model.addAttribute("cartCount", cart.getTotalQuantity());
        return "shop/cart";
    }

    // --------------------------------------------------------------- Checkout

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        Cart cart = cart(session);
        if (cart.isEmpty()) {
            return "redirect:/shop/cart";
        }
        CartView view = buildCartView(cart);
        List<ShippingMethod> methods = shippingMethodService.all();
        model.addAttribute("lines", view.lines);
        model.addAttribute("subtotal", view.subtotal);
        model.addAttribute("discount", view.discount);
        model.addAttribute("couponCode", cart.getCouponCode());
        model.addAttribute("shippingMethods", methods);
        model.addAttribute("cartCount", cart.getTotalQuantity());
        return "shop/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam(required = false) String customerEmail,
                             @RequestParam String shippingAddress,
                             @RequestParam String paymentMethod,
                             @RequestParam(required = false) UUID shippingMethodId,
                             HttpSession session, RedirectAttributes ra) {
        Cart cart = cart(session);
        if (cart.isEmpty()) {
            return "redirect:/shop/cart";
        }
        CartView view = buildCartView(cart);

        ShippingMethod method = shippingMethodId != null ? shippingMethodService.get(shippingMethodId) : null;
        BigDecimal shipping = shippingMethodService.chargeFor(method, view.subtotal);
        BigDecimal total = view.subtotal.subtract(view.discount).add(shipping);

        Order order = Order.builder()
                .orderNumber(orderService.nextOrderNumber())
                .customerName(customerName)
                .customerEmail(customerEmail)
                .status(OrderStatus.PENDING)
                .subtotal(view.subtotal)
                .discount(view.discount)
                .shipping(shipping)
                .total(total)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .couponCode(cart.getCouponCode())
                .build();
        for (CartLine line : view.lines) {
            order.addItem(OrderItem.builder()
                    .productName(line.getProductName())
                    .quantity(line.getQuantity())
                    .unitPrice(line.getUnitPrice())
                    .lineTotal(line.getLineTotal())
                    .build());
        }
        Order saved = orderService.save(order);

        // Increment coupon usage if one was applied.
        if (cart.getCouponCode() != null) {
            couponService.findUsable(cart.getCouponCode())
                    .ifPresent(c -> couponService.incrementUsage(c.getId()));
        }

        // Upsert a customer record keyed by email so the admin Customers list reflects the order.
        if (customerEmail != null && !customerEmail.isBlank()) {
            Customer customer = customerRepository.findByEmailIgnoreCase(customerEmail.trim())
                    .orElseGet(() -> Customer.builder()
                            .name(customerName).email(customerEmail.trim())
                            .address(shippingAddress).build());
            customer.setName(customerName);
            customer.setAddress(shippingAddress);
            customer.setOrdersCount(customer.getOrdersCount() + 1);
            customer.setTotalSpent(customer.getTotalSpent().add(total));
            customerRepository.save(customer);
        }

        cart.clear();
        session.setAttribute(CART_KEY, cart);
        return "redirect:/shop/confirmation/" + saved.getId();
    }

    @GetMapping("/confirmation/{id:[0-9a-fA-F-]{36}}")
    public String confirmation(@PathVariable UUID id, Model model, HttpSession session) {
        Order order = orderService.get(id);
        model.addAttribute("order", order);
        model.addAttribute("cartCount", cart(session).getTotalQuantity());
        return "shop/confirmation";
    }

    // --------------------------------------------------------------- Helpers

    /** Resolves session cart entries into priced display lines + totals. */
    private CartView buildCartView(Cart cart) {
        CartView view = new CartView();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : new LinkedHashMap<>(cart.getItems()).entrySet()) {
            UUID productId;
            try {
                productId = UUID.fromString(entry.getKey());
            } catch (IllegalArgumentException ex) {
                continue;
            }
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                continue;
            }
            Product product = productOpt.get();
            int qty = entry.getValue();
            BigDecimal unitPrice = product.getSellingPrice() != null ? product.getSellingPrice() : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            view.lines.add(new CartLine(productId, product.getName(), product.getImage(),
                    qty, unitPrice, lineTotal));
            subtotal = subtotal.add(lineTotal);
        }
        view.subtotal = subtotal;
        if (cart.getCouponCode() != null) {
            BigDecimal finalSubtotal = subtotal;
            view.discount = couponService.findUsable(cart.getCouponCode())
                    .map(c -> couponService.discountFor(c, finalSubtotal))
                    .orElse(BigDecimal.ZERO);
        }
        return view;
    }

    private static class CartView {
        List<CartLine> lines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
    }
}
