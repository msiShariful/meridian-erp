package com.erp.support.service;

import com.erp.support.entity.*;
import com.erp.support.enums.TicketPriority;
import com.erp.support.enums.TicketStatus;
import com.erp.support.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds representative Support / Help Desk data (categories, SLA policies, tickets
 * with conversation threads, knowledge-base articles and FAQs) on first startup.
 */
@Slf4j
@Component
@Order(6)
@RequiredArgsConstructor
public class SupportDataInitializer {

    private final TicketCategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final SLARepository slaRepository;
    private final KBArticleRepository articleRepository;
    private final FAQRepository faqRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(6)
    @Transactional
    public void seed() {
        if (ticketRepository.count() > 0) {
            return;
        }
        log.info("Seeding Support demo data...");

        // ---- Categories ----
        String[][] categoryData = {
                {"Billing", "Invoices, payments, refunds and subscription questions."},
                {"Technical", "Bugs, outages, integrations and product errors."},
                {"Account", "Login, profile, permissions and access requests."},
                {"Feature Request", "Ideas and enhancement suggestions from customers."},
                {"General", "Anything that does not fit another category."}
        };
        List<TicketCategory> categories = new ArrayList<>();
        for (String[] c : categoryData) {
            categories.add(categoryRepository.save(TicketCategory.builder()
                    .name(c[0]).description(c[1]).build()));
        }

        // ---- SLA policies (one per priority) ----
        for (TicketPriority p : TicketPriority.values()) {
            slaRepository.save(SLA.builder()
                    .name(p.getLabel() + " Priority SLA")
                    .priority(p)
                    .responseHours(p.getDefaultResponseHours())
                    .resolutionHours(p.getDefaultResolutionHours())
                    .build());
        }

        // ---- Tickets ----
        String[] customers = {
                "Anwar Hossain", "Sharmin Akter", "Rafiqul Islam", "Nusrat Jahan",
                "Mizanur Rahman", "Tania Sultana", "Imran Khan", "Sadia Afrin",
                "Habibur Rahman", "Farzana Begum", "Sajjad Hossain", "Mukti Rani",
                "Asif Mahmud", "Rabeya Khatun", "Jamil Ahmed", "Sumona Das",
                "Kamrul Hasan", "Naima Tabassum", "Delwar Hossain", "Ishrat Jahan"
        };
        String[] subjects = {
                "Invoice amount does not match my plan",
                "Cannot log in after password reset",
                "Dashboard charts not loading",
                "Request refund for duplicate charge",
                "API returns 500 on bulk export",
                "Add dark mode to the reports page",
                "How do I add a new team member?",
                "Mobile app crashes on startup",
                "Upgrade my subscription to Enterprise",
                "Email notifications stopped arriving",
                "Data import fails with CSV files",
                "Need help configuring SSO",
                "Feature: bulk-edit for tickets",
                "Two-factor authentication setup issue",
                "Billing address change request",
                "Slow page load in the evening",
                "Cannot delete an archived project",
                "Request additional storage quota",
                "Webhook deliveries are delayed",
                "General question about data retention"
        };
        TicketStatus[] statuses = TicketStatus.values();
        TicketPriority[] priorities = TicketPriority.values();
        String[] agents = {"Tanvir Ahmed", "Priya Saha", "Rakib Hasan", "Lubna Karim"};

        LocalDateTime now = LocalDateTime.now();
        int seededTickets = 0;
        for (int i = 0; i < subjects.length; i++) {
            TicketStatus status = statuses[i % statuses.length];
            TicketPriority priority = priorities[i % priorities.length];
            String customer = customers[i];
            String agent = agents[i % agents.length];
            TicketCategory category = categories.get(i % categories.size());

            boolean terminal = status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED;
            // Make every third open ticket old enough to breach its SLA.
            long ageHours = (!terminal && i % 3 == 0)
                    ? priority.getDefaultResolutionHours() + 24L
                    : (long) (i % 12);

            Ticket ticket = Ticket.builder()
                    .ticketNumber(String.format("TKT-%04d", i + 1))
                    .subject(subjects[i])
                    .description("Customer reported: " + subjects[i] + ". Please investigate and respond.")
                    .customerName(customer)
                    .customerEmail(customer.toLowerCase().replace(" ", ".") + "@example.com")
                    .category(category)
                    .priority(priority)
                    .status(status)
                    .assignedTo(agent)
                    .slaResponseHours(priority.getDefaultResponseHours())
                    .slaResolutionHours(priority.getDefaultResolutionHours())
                    .build();

            // First customer message.
            ticket.addMessage(TicketMessage.builder()
                    .author(customer)
                    .body("Hello, " + subjects[i].toLowerCase() + ". Could you please help me with this?")
                    .internalNote(false)
                    .sentAt(now.minusHours(ageHours))
                    .build());

            // Agent reply for non-OPEN tickets.
            if (status != TicketStatus.OPEN) {
                ticket.addMessage(TicketMessage.builder()
                        .author(agent)
                        .body("Hi " + customer.split(" ")[0] + ", thanks for reaching out. We are looking into this now.")
                        .internalNote(false)
                        .sentAt(now.minusHours(Math.max(0, ageHours - 1)))
                        .build());
                ticket.setFirstResponseAt(now.minusHours(Math.max(0, ageHours - 1)));
            }

            // Internal note on some tickets (third message).
            if (i % 4 == 0) {
                ticket.addMessage(TicketMessage.builder()
                        .author(agent)
                        .body("Internal: escalated to the engineering team for review.")
                        .internalNote(true)
                        .sentAt(now.minusHours(Math.max(0, ageHours - 2)))
                        .build());
            }

            if (terminal) {
                ticket.setResolvedAt(now.minusHours(Math.max(0, ageHours - 3)));
            }

            ticketRepository.save(ticket);
            seededTickets++;
        }

        // ---- Knowledge Base articles ----
        String[][] articleData = {
                {"Getting Started with Meridian ERP", "General", "Welcome to Meridian ERP. This guide walks you through your first login, navigating the dashboard, and setting up your company profile.", "onboarding,setup,getting-started"},
                {"How to Reset Your Password", "Account", "If you have forgotten your password, click 'Forgot password' on the login screen and follow the email instructions. Reset links expire after 30 minutes.", "password,login,security"},
                {"Understanding Your Invoice", "Billing", "Your monthly invoice lists the subscription plan, per-seat charges and any add-ons. Taxes are applied based on your billing address.", "billing,invoice,payments"},
                {"Configuring Single Sign-On (SSO)", "Technical", "Meridian supports SAML 2.0 SSO. Navigate to Settings > Security, upload your IdP metadata and map the required attributes.", "sso,saml,security,integration"},
                {"Importing Data via CSV", "Technical", "You can bulk-import records using CSV. Download the template, match the columns, and upload. Files must be UTF-8 encoded and under 10 MB.", "import,csv,data"},
                {"Managing Team Members and Roles", "Account", "Admins can invite team members from Settings > Users. Assign roles to control access to each module.", "users,roles,permissions"},
                {"Setting Up Email Notifications", "Technical", "Choose which events trigger emails under Settings > Notifications. Verify your domain to improve deliverability.", "notifications,email,settings"},
                {"Requesting a Refund", "Billing", "Eligible refunds are processed within 5-7 business days to the original payment method. Open a ticket with your invoice number to start.", "refund,billing,payments"}
        };
        for (int i = 0; i < articleData.length; i++) {
            String[] a = articleData[i];
            articleRepository.save(KBArticle.builder()
                    .title(a[0]).category(a[1]).body(a[2]).tags(a[3])
                    .published(i % 5 != 4)
                    .helpfulYes((i + 3) * 4)
                    .helpfulNo(i % 3)
                    .build());
        }

        // ---- FAQs ----
        String[][] faqData = {
                {"How do I upgrade my plan?", "From Settings > Billing, choose a new plan and confirm. Changes are prorated automatically.", "Billing"},
                {"What payment methods are accepted?", "We accept major credit/debit cards and bank transfers for annual plans.", "Billing"},
                {"Is my data encrypted?", "Yes. All data is encrypted in transit with TLS and at rest with AES-256.", "Technical"},
                {"Do you offer an API?", "Yes, a REST API is available on Business and Enterprise plans. See the developer docs.", "Technical"},
                {"How do I export my data?", "Go to Settings > Data and request a full export; you will receive a download link by email.", "Technical"},
                {"Can I have multiple companies?", "Enterprise plans support multiple company workspaces under one account.", "Account"},
                {"How do I delete my account?", "Account owners can request deletion from Settings > Account. Data is purged after 30 days.", "Account"},
                {"What are your support hours?", "Standard support is available 9am-9pm (BST), Sunday to Thursday. Enterprise plans include 24/7 support.", "General"},
                {"How long do you retain backups?", "Backups are retained for 35 days on rolling basis across geographically separate regions.", "General"},
                {"Can I customize roles?", "Yes, custom roles with granular permissions are available on Business and Enterprise plans.", "Account"}
        };
        for (String[] f : faqData) {
            faqRepository.save(FAQ.builder()
                    .question(f[0]).answer(f[1]).category(f[2]).build());
        }

        log.info("Support demo data seeded: {} categories, {} tickets, {} KB articles, {} FAQs.",
                categories.size(), seededTickets, articleData.length, faqData.length);
    }
}
