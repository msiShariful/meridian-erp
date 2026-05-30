package com.erp.dashboard;

import com.erp.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Aggregates cross-module KPIs for the dashboard. As each business module is built
 * its repository is wired in here; until then representative figures are supplied so
 * the dashboard renders a complete picture for demos.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;

    public DashboardData buildDashboard() {
        long activeUsers = userRepository.countByEnabledTrue();

        List<Kpi> kpis = List.of(
                new Kpi("Revenue (MTD)", "৳ 4,82,500", "+12.4%", true, "currency", "brand"),
                new Kpi("Active Employees", String.valueOf(Math.max(activeUsers, 1)), "+3", true, "users", "success"),
                new Kpi("Open Orders", "37", "+8", true, "cart", "warning"),
                new Kpi("Support Tickets", "14", "-5", false, "ticket", "danger"),
                new Kpi("New Leads", "26", "+19%", true, "spark", "brand"),
                new Kpi("Low Stock Alerts", "9", "+2", false, "alert", "danger")
        );

        List<String> months = List.of("Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                "Dec", "Jan", "Feb", "Mar", "Apr", "May");
        List<Integer> revenue = List.of(320, 340, 360, 355, 390, 410, 445, 430, 460, 470, 455, 482);

        Map<String, Integer> salesByCategory = new java.util.LinkedHashMap<>();
        salesByCategory.put("Electronics", 42);
        salesByCategory.put("Apparel", 23);
        salesByCategory.put("Home & Living", 18);
        salesByCategory.put("Groceries", 11);
        salesByCategory.put("Other", 6);

        List<RecentOrder> recentOrders = List.of(
                new RecentOrder("ORD-1042", "Ayesha Siddiqua", "৳ 12,400", "Delivered"),
                new RecentOrder("ORD-1041", "Karim Traders", "৳ 8,900", "Processing"),
                new RecentOrder("ORD-1040", "Nabila Rahman", "৳ 3,250", "Shipped"),
                new RecentOrder("ORD-1039", "Hasan & Sons", "৳ 21,750", "Pending"),
                new RecentOrder("ORD-1038", "Dhaka Mart", "৳ 5,600", "Delivered")
        );

        List<Activity> activities = List.of(
                new Activity("Nusrat Jahan", "approved a leave request", "2h ago"),
                new Activity("Rakib Hasan", "moved deal 'Beximco Retail' to Won", "4h ago"),
                new Activity("Imran Khan", "received PO-2087 into Main Warehouse", "6h ago"),
                new Activity("Sadia Islam", "resolved ticket #TKT-318", "8h ago"),
                new Activity("Farzana Akter", "issued invoice INV-0091", "yesterday")
        );

        Attendance attendance = new Attendance(48, 5, 3);

        return new DashboardData(kpis, months, revenue, salesByCategory,
                recentOrders, activities, attendance);
    }

    public record Kpi(String label, String value, String delta, boolean positive, String icon, String color) {}
    public record RecentOrder(String reference, String customer, String amount, String status) {}
    public record Activity(String actor, String action, String time) {}
    public record Attendance(int present, int absent, int leave) {}
    public record DashboardData(List<Kpi> kpis, List<String> months, List<Integer> revenue,
                                Map<String, Integer> salesByCategory, List<RecentOrder> recentOrders,
                                List<Activity> activities, Attendance attendance) {}
}
