package com.erp.accounting.service;

import com.erp.accounting.entity.*;
import com.erp.accounting.enums.AccountType;
import com.erp.accounting.enums.ExpenseStatus;
import com.erp.accounting.enums.InvoiceStatus;
import com.erp.accounting.enums.PaymentMethod;
import com.erp.accounting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Seeds representative Accounting data (chart of accounts, invoices with items,
 * payments, expenses, balanced journal entries, budgets, tax rates) on first
 * startup so the module is immediately explorable.
 */
@Slf4j
@Component
@Order(5)
@RequiredArgsConstructor
public class AccountingDataInitializer {

    private final AccountRepository accountRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final BudgetRepository budgetRepository;
    private final TaxRateRepository taxRateRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(5)
    @Transactional
    public void seed() {
        if (accountRepository.count() > 0) {
            return;
        }
        log.info("Seeding Accounting demo data...");

        // ---- Chart of accounts ----
        String[][] accountData = {
                {"1000", "Cash", "ASSET", "Cash on hand"},
                {"1010", "Bank - BRAC", "ASSET", "Primary operating bank account"},
                {"1100", "Accounts Receivable", "ASSET", "Amounts owed by customers"},
                {"1200", "Inventory", "ASSET", "Goods held for sale"},
                {"1500", "Fixed Assets", "ASSET", "Equipment and machinery"},
                {"2000", "Accounts Payable", "LIABILITY", "Amounts owed to suppliers"},
                {"2100", "Loans Payable", "LIABILITY", "Outstanding bank loans"},
                {"3000", "Owner Equity", "EQUITY", "Owner's capital contribution"},
                {"4000", "Sales Revenue", "REVENUE", "Income from product sales"},
                {"4100", "Service Revenue", "REVENUE", "Income from services rendered"},
                {"5000", "Salaries Expense", "EXPENSE", "Employee salaries"},
                {"5100", "Rent Expense", "EXPENSE", "Office and warehouse rent"},
                {"5200", "Utilities Expense", "EXPENSE", "Electricity, water, internet"}
        };
        Map<String, Account> accounts = new HashMap<>();
        for (String[] a : accountData) {
            Account account = accountRepository.save(Account.builder()
                    .code(a[0]).name(a[1]).type(AccountType.valueOf(a[2])).description(a[3])
                    .build());
            accounts.put(a[0], account);
        }

        // ---- Tax rates ----
        taxRateRepository.save(TaxRate.builder().name("VAT").rate(new BigDecimal("15.00")).build());
        taxRateRepository.save(TaxRate.builder().name("AIT").rate(new BigDecimal("5.00")).build());

        // ---- Invoices with items ----
        String[] customers = {"Beximco Group", "Grameenphone", "Square Pharmaceuticals", "Walton Hi-Tech",
                "Pran-RFL Group", "BRAC Bank", "Daraz Bangladesh", "Robi Axiata", "ACI Limited",
                "Bashundhara Group", "City Group", "Meghna Group", "Akij Group", "Nestle Bangladesh",
                "Unilever Bangladesh"};
        String[][] itemCatalog = {
                {"ERP License - Annual", "Implementation Services", "Premium Support"},
                {"Cloud Hosting - Monthly", "Data Migration", "Training Workshop"},
                {"CRM Module", "Custom Integration", "Onboarding"},
                {"POS Terminal", "Inventory Module", "Maintenance Contract"}
        };
        InvoiceStatus[] statuses = InvoiceStatus.values();
        List<Invoice> invoices = new ArrayList<>();
        for (int i = 0; i < customers.length; i++) {
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(String.format("INV-%04d", i + 1));
            invoice.setCustomerName(customers[i]);
            invoice.setIssueDate(LocalDate.now().minusDays(10L * (i + 1)));
            invoice.setDueDate(invoice.getIssueDate().plusDays(30));
            invoice.setStatus(statuses[i % statuses.length]);
            invoice.setTaxRate(new BigDecimal("15.00"));
            invoice.setDiscount(BigDecimal.valueOf((i % 3) * 5000L));

            String[] catalog = itemCatalog[i % itemCatalog.length];
            BigDecimal subtotal = BigDecimal.ZERO;
            int itemCount = 2 + (i % 2);
            for (int j = 0; j < itemCount; j++) {
                int qty = 1 + (j % 3);
                BigDecimal unit = BigDecimal.valueOf((j + 1) * 25000L + i * 1000L);
                BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(qty));
                InvoiceItem item = InvoiceItem.builder()
                        .invoice(invoice)
                        .description(catalog[j % catalog.length])
                        .quantity(qty)
                        .unitPrice(unit)
                        .lineTotal(lineTotal)
                        .build();
                invoice.getItems().add(item);
                subtotal = subtotal.add(lineTotal);
            }
            BigDecimal taxAmount = subtotal.multiply(invoice.getTaxRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            invoice.setSubtotal(subtotal);
            invoice.setTaxAmount(taxAmount);
            invoice.setTotal(subtotal.add(taxAmount).subtract(invoice.getDiscount()));
            invoices.add(invoiceRepository.save(invoice));
        }

        // ---- Payments for some invoices ----
        PaymentMethod[] methods = PaymentMethod.values();
        for (int i = 0; i < invoices.size(); i++) {
            Invoice invoice = invoices.get(i);
            if (invoice.getStatus() == InvoiceStatus.PAID) {
                paymentRepository.save(Payment.builder()
                        .invoice(invoice).amount(invoice.getTotal())
                        .date(invoice.getIssueDate().plusDays(5))
                        .method(methods[i % methods.length])
                        .reference("TXN-" + (1000 + i)).build());
            } else if (invoice.getStatus() == InvoiceStatus.PARTIAL) {
                paymentRepository.save(Payment.builder()
                        .invoice(invoice)
                        .amount(invoice.getTotal().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP))
                        .date(invoice.getIssueDate().plusDays(7))
                        .method(methods[i % methods.length])
                        .reference("TXN-" + (1000 + i)).build());
            }
        }
        // A couple of standalone payments
        paymentRepository.save(Payment.builder().amount(new BigDecimal("12000.00"))
                .date(LocalDate.now().minusDays(3)).method(PaymentMethod.CASH)
                .reference("Advance from new client").build());

        // ---- Expense categories ----
        String[] categoryNames = {"Travel", "Office Supplies", "Meals & Entertainment", "Utilities", "Software"};
        List<ExpenseCategory> categories = new ArrayList<>();
        for (String name : categoryNames) {
            categories.add(expenseCategoryRepository.save(ExpenseCategory.builder().name(name).build()));
        }

        // ---- Expenses ----
        String[] expenseDescriptions = {"Client visit to Chittagong", "A4 paper and stationery",
                "Team lunch with vendor", "Office electricity bill", "Annual SaaS subscription",
                "Airfare Dhaka-Sylhet", "Printer toner cartridges", "Dinner with client",
                "Internet broadband - monthly", "Design tool license", "Hotel stay - conference",
                "Whiteboard markers", "Coffee supplies", "Water bill", "Cloud storage upgrade"};
        String[] claimants = {"Rakib Hasan", "Nadia Rahman", "Tariq Aziz", "Shabnam Ferdousi", "Mahbub Alam"};
        ExpenseStatus[] expenseStatuses = ExpenseStatus.values();
        for (int i = 0; i < expenseDescriptions.length; i++) {
            expenseRepository.save(Expense.builder()
                    .category(categories.get(i % categories.size()))
                    .description(expenseDescriptions[i])
                    .amount(BigDecimal.valueOf((i + 1) * 1500L + 500L))
                    .date(LocalDate.now().minusDays(i + 1))
                    .status(expenseStatuses[i % expenseStatuses.length])
                    .claimedBy(claimants[i % claimants.length])
                    .receipt("receipt-" + (1000 + i) + ".pdf")
                    .build());
        }

        // ---- Balanced journal entries ----
        seedJournalEntry("JE-0001", "Owner capital injection", LocalDate.now().minusDays(40),
                new Object[][]{
                        {accounts.get("1010"), "500000.00", "0.00"},
                        {accounts.get("3000"), "0.00", "500000.00"}
                });
        seedJournalEntry("JE-0002", "Cash sale recorded", LocalDate.now().minusDays(20),
                new Object[][]{
                        {accounts.get("1000"), "115000.00", "0.00"},
                        {accounts.get("4000"), "0.00", "100000.00"},
                        {accounts.get("2000"), "0.00", "15000.00"}
                });
        seedJournalEntry("JE-0003", "Monthly rent payment", LocalDate.now().minusDays(15),
                new Object[][]{
                        {accounts.get("5100"), "60000.00", "0.00"},
                        {accounts.get("1010"), "0.00", "60000.00"}
                });
        seedJournalEntry("JE-0004", "Salary disbursement", LocalDate.now().minusDays(5),
                new Object[][]{
                        {accounts.get("5000"), "250000.00", "0.00"},
                        {accounts.get("1010"), "0.00", "250000.00"}
                });

        // ---- Budgets ----
        String[][] budgetData = {
                {"Salaries", "FY2026 Q1", "750000", "720000"},
                {"Rent", "FY2026 Q1", "180000", "180000"},
                {"Marketing", "FY2026 Q1", "300000", "345000"},
                {"Travel", "FY2026 Q1", "120000", "98000"},
                {"Utilities", "FY2026 Q1", "90000", "84000"},
                {"Software", "FY2026 Q1", "150000", "162000"}
        };
        for (String[] b : budgetData) {
            budgetRepository.save(Budget.builder()
                    .categoryName(b[0]).period(b[1])
                    .budgetedAmount(new BigDecimal(b[2]))
                    .actualAmount(new BigDecimal(b[3]))
                    .build());
        }

        log.info("Accounting demo data seeded: {} accounts, {} invoices, {} expenses.",
                accountData.length, invoices.size(), expenseDescriptions.length);
    }

    private void seedJournalEntry(String reference, String description, LocalDate date, Object[][] lineData) {
        JournalEntry entry = JournalEntry.builder()
                .reference(reference).description(description).date(date).build();
        BigDecimal totalDebit = BigDecimal.ZERO;
        for (Object[] l : lineData) {
            BigDecimal debit = new BigDecimal((String) l[1]);
            BigDecimal credit = new BigDecimal((String) l[2]);
            entry.getLines().add(JournalEntryLine.builder()
                    .entry(entry).account((Account) l[0]).debit(debit).credit(credit).build());
            totalDebit = totalDebit.add(debit);
        }
        entry.setTotalAmount(totalDebit);
        journalEntryRepository.save(entry);
    }
}
