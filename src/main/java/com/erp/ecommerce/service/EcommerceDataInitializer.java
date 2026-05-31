package com.erp.ecommerce.service;

import com.erp.ecommerce.entity.Coupon;
import com.erp.ecommerce.entity.Customer;
import com.erp.ecommerce.entity.Order;
import com.erp.ecommerce.entity.OrderItem;
import com.erp.ecommerce.entity.Review;
import com.erp.ecommerce.entity.ShippingMethod;
import com.erp.ecommerce.enums.CouponType;
import com.erp.ecommerce.enums.OrderStatus;
import com.erp.ecommerce.enums.ReviewStatus;
import com.erp.ecommerce.repository.CouponRepository;
import com.erp.ecommerce.repository.CustomerRepository;
import com.erp.ecommerce.repository.OrderRepository;
import com.erp.ecommerce.repository.ReviewRepository;
import com.erp.ecommerce.repository.ShippingMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds representative E-Commerce data (customers, coupons, orders with items,
 * product reviews, shipping methods) on first startup so the module and the
 * public storefront are immediately explorable.
 */
@Slf4j
@Component
@org.springframework.core.annotation.Order(9)
@RequiredArgsConstructor
public class EcommerceDataInitializer {

    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final ShippingMethodRepository shippingMethodRepository;

    @EventListener(ApplicationReadyEvent.class)
    @org.springframework.core.annotation.Order(9)
    @Transactional
    public void seed() {
        if (orderRepository.count() > 0) {
            return;
        }
        log.info("Seeding E-Commerce demo data...");

        // -------------------------------------------------- Shipping methods
        List<ShippingMethod> shipping = new ArrayList<>();
        shipping.add(shippingMethodRepository.save(ShippingMethod.builder()
                .name("Standard Delivery").rate(BigDecimal.valueOf(60)).freeAbove(BigDecimal.valueOf(5000)).build()));
        shipping.add(shippingMethodRepository.save(ShippingMethod.builder()
                .name("Express Delivery").rate(BigDecimal.valueOf(150)).freeAbove(null).build()));
        shipping.add(shippingMethodRepository.save(ShippingMethod.builder()
                .name("Free Pickup").rate(BigDecimal.ZERO).freeAbove(BigDecimal.ZERO).build()));

        // -------------------------------------------------------- Coupons
        couponRepository.save(Coupon.builder().code("EID2026").type(CouponType.PERCENT)
                .value(BigDecimal.valueOf(10)).minOrderValue(BigDecimal.valueOf(1000))
                .usageLimit(100).usedCount(12).expiryDate(LocalDate.now().plusMonths(2)).active(true).build());
        couponRepository.save(Coupon.builder().code("FLAT500").type(CouponType.FIXED)
                .value(BigDecimal.valueOf(500)).minOrderValue(BigDecimal.valueOf(3000))
                .usageLimit(50).usedCount(8).expiryDate(LocalDate.now().plusMonths(1)).active(true).build());
        couponRepository.save(Coupon.builder().code("WELCOME15").type(CouponType.PERCENT)
                .value(BigDecimal.valueOf(15)).minOrderValue(BigDecimal.valueOf(500))
                .usageLimit(200).usedCount(45).expiryDate(LocalDate.now().plusMonths(6)).active(true).build());
        couponRepository.save(Coupon.builder().code("FREESHIP").type(CouponType.FIXED)
                .value(BigDecimal.valueOf(150)).minOrderValue(BigDecimal.valueOf(2000))
                .usageLimit(0).usedCount(30).expiryDate(LocalDate.now().plusMonths(3)).active(true).build());
        couponRepository.save(Coupon.builder().code("WINTER20").type(CouponType.PERCENT)
                .value(BigDecimal.valueOf(20)).minOrderValue(BigDecimal.valueOf(1500))
                .usageLimit(40).usedCount(40).expiryDate(LocalDate.now().minusDays(10)).active(false).build());

        // ------------------------------------------------------- Customers
        String[][] customerData = {
                {"Ariful Haque", "ariful.haque@gmail.com", "+8801711100001", "House 12, Road 5, Dhanmondi, Dhaka"},
                {"Sumaiya Akter", "sumaiya.akter@gmail.com", "+8801711100002", "Flat 4B, Gulshan 2, Dhaka"},
                {"Mehedi Hasan", "mehedi.hasan@gmail.com", "+8801711100003", "Sector 7, Uttara, Dhaka"},
                {"Nusrat Jahan", "nusrat.jahan@gmail.com", "+8801711100004", "Agrabad C/A, Chittagong"},
                {"Tanvir Ahmed", "tanvir.ahmed@gmail.com", "+8801711100005", "Zindabazar, Sylhet"},
                {"Farhana Yeasmin", "farhana.yeasmin@gmail.com", "+8801711100006", "Boalia, Rajshahi"},
                {"Sabbir Rahman", "sabbir.rahman@gmail.com", "+8801711100007", "Kazir Dewri, Chittagong"},
                {"Israt Jahan", "israt.jahan@gmail.com", "+8801711100008", "Mirpur 10, Dhaka"},
                {"Rakibul Islam", "rakibul.islam@gmail.com", "+8801711100009", "Bashundhara R/A, Dhaka"},
                {"Mahmuda Khatun", "mahmuda.khatun@gmail.com", "+8801711100010", "Khulshi, Chittagong"},
                {"Jubayer Alam", "jubayer.alam@gmail.com", "+8801711100011", "Banani, Dhaka"},
                {"Sharmin Sultana", "sharmin.sultana@gmail.com", "+8801711100012", "Shaheb Bazar, Rajshahi"},
                {"Kamrul Hasan", "kamrul.hasan@gmail.com", "+8801711100013", "Mohakhali, Dhaka"},
                {"Tahmina Begum", "tahmina.begum@gmail.com", "+8801711100014", "Amberkhana, Sylhet"},
                {"Naimur Rahman", "naimur.rahman@gmail.com", "+8801711100015", "Sonadanga, Khulna"}
        };
        List<Customer> customers = new ArrayList<>();
        for (String[] c : customerData) {
            customers.add(customerRepository.save(Customer.builder()
                    .name(c[0]).email(c[1]).phone(c[2]).address(c[3])
                    .totalSpent(BigDecimal.ZERO).ordersCount(0).build()));
        }

        // ---------------------------------------------------------- Orders
        // product: name, unit price
        String[][] catalog = {
                {"Samsung Galaxy A55", "48000"}, {"Wireless Earbuds Pro", "2900"},
                {"Men's Cotton Polo Shirt", "750"}, {"Women's Denim Jeans", "1250"},
                {"Basmati Rice 5kg", "780"}, {"Office Desk Chair", "7200"},
                {"LED Desk Lamp", "1200"}, {"A4 Paper Ream", "360"},
                {"Cordless Drill", "4800"}, {"Bluetooth Speaker", "2200"},
                {"Winter Hoodie", "1400"}, {"Soybean Oil 5L", "920"}
        };
        OrderStatus[] statuses = OrderStatus.values();
        String[] payments = {"COD", "Card", "bKash", "Nagad"};

        for (int i = 0; i < 20; i++) {
            Customer customer = customers.get(i % customers.size());
            OrderStatus status = statuses[i % statuses.length];

            Order order = Order.builder()
                    .orderNumber(String.format("ORD-%04d", i + 1))
                    .customerName(customer.getName())
                    .customerEmail(customer.getEmail())
                    .status(status)
                    .shippingAddress(customer.getAddress())
                    .paymentMethod(payments[i % payments.length])
                    .build();

            // 1-3 items per order
            int itemCount = (i % 3) + 1;
            BigDecimal subtotal = BigDecimal.ZERO;
            for (int j = 0; j < itemCount; j++) {
                String[] prod = catalog[(i + j) % catalog.length];
                int qty = (j % 2) + 1;
                BigDecimal unitPrice = new BigDecimal(prod[1]);
                BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
                order.addItem(OrderItem.builder()
                        .productName(prod[0]).quantity(qty)
                        .unitPrice(unitPrice).lineTotal(lineTotal).build());
                subtotal = subtotal.add(lineTotal);
            }

            BigDecimal discount = (i % 4 == 0)
                    ? subtotal.multiply(BigDecimal.valueOf(10)).divide(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
            BigDecimal shippingCharge = subtotal.compareTo(BigDecimal.valueOf(5000)) >= 0
                    ? BigDecimal.ZERO : BigDecimal.valueOf(60);
            BigDecimal total = subtotal.subtract(discount).add(shippingCharge);

            order.setSubtotal(subtotal);
            order.setDiscount(discount);
            order.setShipping(shippingCharge);
            order.setTotal(total);
            if (i % 4 == 0) order.setCouponCode("EID2026");
            orderRepository.save(order);

            // Keep customer aggregates roughly consistent (skip cancelled/refunded).
            if (status != OrderStatus.CANCELLED && status != OrderStatus.REFUNDED) {
                customer.setOrdersCount(customer.getOrdersCount() + 1);
                customer.setTotalSpent(customer.getTotalSpent().add(total));
            }
        }
        customerRepository.saveAll(customers);

        // --------------------------------------------------------- Reviews
        String[][] reviewData = {
                {"Samsung Galaxy A55", "Ariful Haque", "5", "Excellent phone, great camera and battery life!"},
                {"Wireless Earbuds Pro", "Sumaiya Akter", "4", "Good sound quality but case is a bit bulky."},
                {"Men's Cotton Polo Shirt", "Mehedi Hasan", "5", "Very comfortable fabric, fits perfectly."},
                {"Women's Denim Jeans", "Nusrat Jahan", "3", "Color faded slightly after first wash."},
                {"Basmati Rice 5kg", "Tanvir Ahmed", "5", "Premium quality rice, smells amazing."},
                {"Office Desk Chair", "Farhana Yeasmin", "4", "Sturdy and comfortable for long hours."},
                {"LED Desk Lamp", "Sabbir Rahman", "5", "Bright and energy efficient, love it."},
                {"Cordless Drill", "Israt Jahan", "4", "Powerful drill, decent battery backup."},
                {"Bluetooth Speaker", "Rakibul Islam", "2", "Sound distorts at high volume."},
                {"Winter Hoodie", "Mahmuda Khatun", "5", "Warm and cozy, perfect for winter."},
                {"Samsung Galaxy A55", "Jubayer Alam", "4", "Smooth performance, delivery was fast."},
                {"Wireless Earbuds Pro", "Sharmin Sultana", "1", "Stopped working after two weeks."},
                {"Basmati Rice 5kg", "Kamrul Hasan", "5", "Best rice for biriyani, highly recommend."},
                {"Office Desk Chair", "Tahmina Begum", "3", "Assembly instructions were unclear."},
                {"Bluetooth Speaker", "Naimur Rahman", "5", "Great bass and long battery life."}
        };
        ReviewStatus[] reviewStatuses = ReviewStatus.values();
        for (int i = 0; i < reviewData.length; i++) {
            String[] r = reviewData[i];
            reviewRepository.save(Review.builder()
                    .productName(r[0]).customerName(r[1])
                    .rating(Integer.parseInt(r[2])).comment(r[3])
                    .status(reviewStatuses[i % reviewStatuses.length])
                    .build());
        }

        log.info("E-Commerce demo data seeded: {} customers, 5 coupons, 20 orders, {} reviews, {} shipping methods.",
                customers.size(), reviewData.length, shipping.size());
    }
}
