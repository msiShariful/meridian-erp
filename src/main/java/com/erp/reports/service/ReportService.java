package com.erp.reports.service;

import com.erp.accounting.entity.Expense;
import com.erp.accounting.entity.Invoice;
import com.erp.accounting.enums.InvoiceStatus;
import com.erp.accounting.repository.ExpenseRepository;
import com.erp.accounting.repository.InvoiceRepository;
import com.erp.crm.entity.Deal;
import com.erp.crm.entity.Lead;
import com.erp.crm.repository.DealRepository;
import com.erp.crm.repository.LeadRepository;
import com.erp.ecommerce.entity.Order;
import com.erp.ecommerce.repository.OrderRepository;
import com.erp.hrm.entity.Employee;
import com.erp.hrm.repository.EmployeeRepository;
import com.erp.inventory.entity.Product;
import com.erp.inventory.repository.ProductRepository;
import com.erp.support.entity.Ticket;
import com.erp.support.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Read-only aggregation service for the cross-module Reports &amp; Analytics module.
 * It reuses the existing module repositories and computes summaries with Java streams;
 * it never mutates data and never depends on writes from other modules.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final InvoiceRepository invoiceRepository;
    private final ExpenseRepository expenseRepository;
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;

    // ----------------------------------------------------------------------
    // Analytics dashboard
    // ----------------------------------------------------------------------

    public AnalyticsData buildAnalytics() {
        List<Lead> leads = leadRepository.findAll();
        List<Deal> deals = dealRepository.findAll();
        List<Invoice> invoices = invoiceRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        List<Ticket> tickets = ticketRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();
        List<Product> products = productRepository.findAll();

        // --- KPI figures ---
        BigDecimal totalRevenue = invoices.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pipelineValue = deals.stream()
                .map(Deal::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long openTickets = tickets.stream()
                .filter(t -> t.getStatus() != com.erp.support.enums.TicketStatus.RESOLVED
                        && t.getStatus() != com.erp.support.enums.TicketStatus.CLOSED)
                .count();
        long lowStock = products.stream()
                .filter(p -> p.getStockQuantity() <= p.getReorderLevel())
                .count();

        List<Kpi> kpis = new ArrayList<>();
        kpis.add(new Kpi("Total Revenue (Paid)", "৳ " + formatMoney(totalRevenue), "brand"));
        kpis.add(new Kpi("Total Expenses", "৳ " + formatMoney(totalExpenses), "danger"));
        kpis.add(new Kpi("Pipeline Value", "৳ " + formatMoney(pipelineValue), "success"));
        kpis.add(new Kpi("Total Leads", String.valueOf(leads.size()), "brand"));
        kpis.add(new Kpi("Orders", String.valueOf(orders.size()), "warning"));
        kpis.add(new Kpi("Open Tickets", String.valueOf(openTickets), "danger"));
        kpis.add(new Kpi("Employees", String.valueOf(employees.size()), "success"));
        kpis.add(new Kpi("Low Stock Items", String.valueOf(lowStock), "warning"));

        // --- Revenue by month (last 12 months, paid invoices by issueDate) ---
        List<String> revenueMonths = new ArrayList<>();
        List<Long> revenueValues = new ArrayList<>();
        YearMonth current = YearMonth.now();
        Map<YearMonth, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            revenueByMonth.put(ym, BigDecimal.ZERO);
        }
        for (Invoice inv : invoices) {
            if (inv.getStatus() != InvoiceStatus.PAID || inv.getIssueDate() == null) continue;
            YearMonth ym = YearMonth.from(inv.getIssueDate());
            if (revenueByMonth.containsKey(ym)) {
                revenueByMonth.put(ym, revenueByMonth.get(ym).add(inv.getTotal()));
            }
        }
        for (Map.Entry<YearMonth, BigDecimal> e : revenueByMonth.entrySet()) {
            revenueMonths.add(e.getKey().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            revenueValues.add(e.getValue().longValue());
        }

        // --- Leads by status (donut) ---
        Map<String, Long> leadsByStatus = new LinkedHashMap<>();
        for (com.erp.crm.enums.LeadStatus s : com.erp.crm.enums.LeadStatus.values()) {
            leadsByStatus.put(s.getLabel(),
                    leads.stream().filter(l -> l.getStatus() == s).count());
        }

        // --- Deals by stage (bar) ---
        Map<String, Long> dealsByStage = new LinkedHashMap<>();
        for (com.erp.crm.enums.DealStage st : com.erp.crm.enums.DealStage.values()) {
            dealsByStage.put(st.getLabel(),
                    deals.stream().filter(d -> d.getStage() == st).count());
        }

        // --- Orders by status ---
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        for (com.erp.ecommerce.enums.OrderStatus os : com.erp.ecommerce.enums.OrderStatus.values()) {
            ordersByStatus.put(os.getLabel(),
                    orders.stream().filter(o -> o.getStatus() == os).count());
        }

        // --- Tickets by status ---
        Map<String, Long> ticketsByStatus = new LinkedHashMap<>();
        for (com.erp.support.enums.TicketStatus ts : com.erp.support.enums.TicketStatus.values()) {
            ticketsByStatus.put(ts.getLabel(),
                    tickets.stream().filter(t -> t.getStatus() == ts).count());
        }

        // --- Employees by department (bar) ---
        Map<String, Long> employeesByDept = new LinkedHashMap<>();
        for (Employee emp : employees) {
            String dept = (emp.getDepartment() != null && emp.getDepartment().getName() != null)
                    ? emp.getDepartment().getName() : "Unassigned";
            employeesByDept.merge(dept, 1L, Long::sum);
        }

        // --- Expenses by category ---
        Map<String, BigDecimal> expensesByCategory = new LinkedHashMap<>();
        for (Expense ex : expenses) {
            String cat = (ex.getCategory() != null && ex.getCategory().getName() != null)
                    ? ex.getCategory().getName() : "Uncategorised";
            expensesByCategory.merge(cat, ex.getAmount() == null ? BigDecimal.ZERO : ex.getAmount(), BigDecimal::add);
        }

        // --- Top products by selling price (top 5) ---
        Map<String, Long> topProducts = new LinkedHashMap<>();
        products.stream()
                .sorted((a, b) -> {
                    BigDecimal av = a.getSellingPrice() == null ? BigDecimal.ZERO : a.getSellingPrice();
                    BigDecimal bv = b.getSellingPrice() == null ? BigDecimal.ZERO : b.getSellingPrice();
                    return bv.compareTo(av);
                })
                .limit(5)
                .forEach(p -> topProducts.put(p.getName(),
                        (p.getSellingPrice() == null ? BigDecimal.ZERO : p.getSellingPrice()).longValue()));

        return new AnalyticsData(kpis, revenueMonths, revenueValues, leadsByStatus, dealsByStage,
                ordersByStatus, ticketsByStatus, employeesByDept, expensesByCategory, topProducts);
    }

    // ----------------------------------------------------------------------
    // Custom report builder
    // ----------------------------------------------------------------------

    public CustomReport buildCustomReport(String module, String from, String to, String groupBy) {
        String mod = (module == null || module.isBlank()) ? "leads" : module.toLowerCase(Locale.ENGLISH);
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);

        List<String> headers;
        List<List<String>> rows = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        String sumLabel = "";

        switch (mod) {
            case "deals" -> {
                headers = List.of("Title", "Stage", "Value", "Probability", "Owner", "Expected Close");
                for (Deal d : dealRepository.findAll()) {
                    if (!inRange(d.getExpectedCloseDate(), fromDate, toDate)) continue;
                    rows.add(List.of(
                            nz(d.getTitle()),
                            d.getStage() == null ? "" : d.getStage().getLabel(),
                            "৳ " + formatMoney(d.getValue()),
                            d.getProbability() + "%",
                            nz(d.getOwner()),
                            d.getExpectedCloseDate() == null ? "" : d.getExpectedCloseDate().toString()));
                    sum = sum.add(d.getValue() == null ? BigDecimal.ZERO : d.getValue());
                }
                sumLabel = "Total Deal Value";
            }
            case "invoices" -> {
                headers = List.of("Invoice #", "Customer", "Issue Date", "Due Date", "Status", "Total");
                for (Invoice i : invoiceRepository.findAll()) {
                    if (!inRange(i.getIssueDate(), fromDate, toDate)) continue;
                    rows.add(List.of(
                            nz(i.getInvoiceNumber()),
                            nz(i.getCustomerName()),
                            i.getIssueDate() == null ? "" : i.getIssueDate().toString(),
                            i.getDueDate() == null ? "" : i.getDueDate().toString(),
                            i.getStatus() == null ? "" : i.getStatus().getLabel(),
                            "৳ " + formatMoney(i.getTotal())));
                    sum = sum.add(i.getTotal() == null ? BigDecimal.ZERO : i.getTotal());
                }
                sumLabel = "Total Invoiced";
            }
            case "orders" -> {
                headers = List.of("Order #", "Customer", "Date", "Status", "Items", "Total");
                for (Order o : orderRepository.findAll()) {
                    if (!inRange(toLocalDate(o.getCreatedAt()), fromDate, toDate)) continue;
                    rows.add(List.of(
                            nz(o.getOrderNumber()),
                            nz(o.getCustomerName()),
                            o.getCreatedAt() == null ? "" : o.getCreatedAt().toLocalDate().toString(),
                            o.getStatus() == null ? "" : o.getStatus().getLabel(),
                            String.valueOf(o.getTotalQuantity()),
                            "৳ " + formatMoney(o.getTotal())));
                    sum = sum.add(o.getTotal() == null ? BigDecimal.ZERO : o.getTotal());
                }
                sumLabel = "Total Order Value";
            }
            case "tickets" -> {
                headers = List.of("Ticket #", "Subject", "Customer", "Priority", "Status", "Created");
                for (Ticket t : ticketRepository.findAll()) {
                    if (!inRange(toLocalDate(t.getCreatedAt()), fromDate, toDate)) continue;
                    rows.add(List.of(
                            nz(t.getTicketNumber()),
                            nz(t.getSubject()),
                            nz(t.getCustomerName()),
                            t.getPriority() == null ? "" : t.getPriority().getLabel(),
                            t.getStatus() == null ? "" : t.getStatus().getLabel(),
                            t.getCreatedAt() == null ? "" : t.getCreatedAt().toLocalDate().toString()));
                }
                sumLabel = "";
            }
            case "expenses" -> {
                headers = List.of("Description", "Category", "Date", "Status", "Claimed By", "Amount");
                for (Expense ex : expenseRepository.findAll()) {
                    if (!inRange(ex.getDate(), fromDate, toDate)) continue;
                    rows.add(List.of(
                            nz(ex.getDescription()),
                            ex.getCategory() == null ? "" : nz(ex.getCategory().getName()),
                            ex.getDate() == null ? "" : ex.getDate().toString(),
                            ex.getStatus() == null ? "" : ex.getStatus().getLabel(),
                            nz(ex.getClaimedBy()),
                            "৳ " + formatMoney(ex.getAmount())));
                    sum = sum.add(ex.getAmount() == null ? BigDecimal.ZERO : ex.getAmount());
                }
                sumLabel = "Total Expenses";
            }
            case "employees" -> {
                headers = List.of("Employee ID", "Name", "Department", "Status", "Join Date", "Basic Salary");
                for (Employee emp : employeeRepository.findAll()) {
                    if (!inRange(emp.getJoinDate(), fromDate, toDate)) continue;
                    rows.add(List.of(
                            nz(emp.getEmployeeId()),
                            nz(emp.getFullName()),
                            emp.getDepartment() == null ? "" : nz(emp.getDepartment().getName()),
                            emp.getStatus() == null ? "" : emp.getStatus().getLabel(),
                            emp.getJoinDate() == null ? "" : emp.getJoinDate().toString(),
                            "৳ " + formatMoney(emp.getBasicSalary())));
                    sum = sum.add(emp.getBasicSalary() == null ? BigDecimal.ZERO : emp.getBasicSalary());
                }
                sumLabel = "Total Basic Salary";
            }
            default -> {
                mod = "leads";
                headers = List.of("Name", "Company", "Source", "Status", "Score", "Est. Value");
                for (Lead l : leadRepository.findAll()) {
                    if (!inRange(toLocalDate(l.getCreatedAt()), fromDate, toDate)) continue;
                    rows.add(List.of(
                            nz(l.getName()),
                            nz(l.getCompanyName()),
                            l.getSource() == null ? "" : l.getSource().getLabel(),
                            l.getStatus() == null ? "" : l.getStatus().getLabel(),
                            String.valueOf(l.getScore()),
                            "৳ " + formatMoney(l.getEstimatedValue())));
                    sum = sum.add(l.getEstimatedValue() == null ? BigDecimal.ZERO : l.getEstimatedValue());
                }
                sumLabel = "Total Estimated Value";
            }
        }

        return new CustomReport(mod, headers, rows, rows.size(),
                sumLabel, "৳ " + formatMoney(sum), from, to, groupBy);
    }

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private static LocalDate toLocalDate(LocalDateTime dt) {
        return dt == null ? null : dt.toLocalDate();
    }

    private static boolean inRange(LocalDate date, LocalDate from, LocalDate to) {
        if (from == null && to == null) return true;
        if (date == null) return false;
        if (from != null && date.isBefore(from)) return false;
        if (to != null && date.isAfter(to)) return false;
        return true;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    public static String formatMoney(BigDecimal v) {
        if (v == null) v = BigDecimal.ZERO;
        return String.format(Locale.ENGLISH, "%,d", v.longValue());
    }

    // ----------------------------------------------------------------------
    // DTOs
    // ----------------------------------------------------------------------

    public record Kpi(String label, String value, String color) {}

    public record AnalyticsData(
            List<Kpi> kpis,
            List<String> revenueMonths,
            List<Long> revenueValues,
            Map<String, Long> leadsByStatus,
            Map<String, Long> dealsByStage,
            Map<String, Long> ordersByStatus,
            Map<String, Long> ticketsByStatus,
            Map<String, Long> employeesByDept,
            Map<String, BigDecimal> expensesByCategory,
            Map<String, Long> topProducts) {}

    public record CustomReport(
            String module,
            List<String> headers,
            List<List<String>> rows,
            int count,
            String sumLabel,
            String sumValue,
            String from,
            String to,
            String groupBy) {}
}
