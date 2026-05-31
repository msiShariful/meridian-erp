package com.erp.crm.service;

import com.erp.crm.entity.*;
import com.erp.crm.enums.*;
import com.erp.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds representative CRM data (companies, contacts, leads, deals, activities,
 * campaigns) on first startup so the module is immediately explorable.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class CrmDataInitializer {

    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final ActivityRepository activityRepository;
    private final CampaignRepository campaignRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    @Transactional
    public void seed() {
        if (companyRepository.count() > 0) {
            return;
        }
        log.info("Seeding CRM demo data...");

        String[][] companyData = {
                {"Beximco Group", "Conglomerate", "500+", "beximco.com"},
                {"Grameenphone", "Telecom", "500+", "grameenphone.com"},
                {"Square Pharmaceuticals", "Pharma", "500+", "squarepharma.com.bd"},
                {"Walton Hi-Tech", "Electronics", "500+", "waltonbd.com"},
                {"Pran-RFL Group", "FMCG", "500+", "pranrfl.com"},
                {"BRAC Bank", "Banking", "201-500", "bracbank.com"},
                {"Daraz Bangladesh", "E-commerce", "201-500", "daraz.com.bd"},
                {"Robi Axiata", "Telecom", "500+", "robi.com.bd"}
        };
        List<Company> companies = new ArrayList<>();
        for (String[] c : companyData) {
            companies.add(companyRepository.save(Company.builder()
                    .name(c[0]).industry(c[1]).companySize(c[2]).website(c[3])
                    .email("info@" + c[3]).phone("+880 2-" + (10000000 + companies.size()))
                    .address("Gulshan, Dhaka 1212").build()));
        }

        String[][] contactData = {
                {"Kamal", "Hossain", "Procurement Head"}, {"Nadia", "Rahman", "CFO"},
                {"Tariq", "Aziz", "IT Director"}, {"Shabnam", "Ferdousi", "Marketing Lead"},
                {"Rashed", "Khan", "Operations Manager"}, {"Lamia", "Chowdhury", "CEO"},
                {"Mahbub", "Alam", "Sales Director"}, {"Tanjina", "Akhter", "HR Head"},
                {"Sabbir", "Ahmed", "CTO"}, {"Rumana", "Islam", "Finance Manager"}
        };
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < contactData.length; i++) {
            String[] ct = contactData[i];
            Company company = companies.get(i % companies.size());
            contacts.add(contactRepository.save(Contact.builder()
                    .firstName(ct[0]).lastName(ct[1]).jobTitle(ct[2])
                    .email(ct[0].toLowerCase() + "." + ct[1].toLowerCase() + "@" + company.getWebsite())
                    .phone("+8801" + (700000000 + i * 11111111L)).company(company)
                    .tags(i % 3 == 0 ? "decision-maker" : "influencer").build()));
        }

        String[] leadNames = {"Arifur Rahman", "Sumaiya Haque", "Jahidul Islam", "Farhana Yasmin",
                "Nazmul Huda", "Tasnim Jahan", "Riadul Karim", "Mehnaz Sultana",
                "Shahriar Kabir", "Anika Tabassum", "Fahim Reza", "Naznin Akter"};
        LeadStatus[] statuses = LeadStatus.values();
        LeadSource[] sources = LeadSource.values();
        for (int i = 0; i < leadNames.length; i++) {
            leadRepository.save(Lead.builder()
                    .name(leadNames[i])
                    .companyName(companyData[i % companyData.length][0])
                    .email(leadNames[i].toLowerCase().replace(" ", ".") + "@example.com")
                    .phone("+8801" + (500000000 + i * 7777777L))
                    .source(sources[i % sources.length])
                    .status(statuses[i % statuses.length])
                    .score((i % 5) + 1)
                    .assignedTo("Rakib Hasan")
                    .estimatedValue(BigDecimal.valueOf((i + 1) * 75000L))
                    .notes("Inbound interest in our enterprise plan.")
                    .build());
        }

        String[] dealTitles = {"Beximco ERP Rollout", "GP Cloud Migration", "Square CRM Upgrade",
                "Walton POS Integration", "Pran Inventory Suite", "BRAC Digital Onboarding",
                "Daraz Logistics Platform", "Robi Analytics Package"};
        DealStage[] dealStages = DealStage.pipeline();
        for (int i = 0; i < dealTitles.length; i++) {
            DealStage stage = dealStages[i % dealStages.length];
            dealRepository.save(Deal.builder()
                    .title(dealTitles[i])
                    .value(BigDecimal.valueOf((i + 2) * 250000L))
                    .stage(stage).probability(stage.getDefaultProbability())
                    .expectedCloseDate(LocalDate.now().plusDays(15L * (i + 1)))
                    .owner("Rakib Hasan")
                    .contact(contacts.get(i % contacts.size()))
                    .company(companies.get(i % companies.size()))
                    .build());
        }

        String[] subjects = {"Discovery call with procurement", "Send proposal draft",
                "Product demo scheduled", "Follow-up on pricing", "Contract review meeting",
                "Quarterly check-in"};
        ActivityType[] types = ActivityType.values();
        for (int i = 0; i < subjects.length; i++) {
            activityRepository.save(Activity.builder()
                    .type(types[i % types.length]).subject(subjects[i])
                    .assignedTo("Rakib Hasan")
                    .dueDate(LocalDateTime.now().plusDays(i))
                    .completed(i % 3 == 0)
                    .notes("Logged from CRM.").build());
        }

        String[][] campaignData = {
                {"Eid Mega Sale 2026", "EMAIL", "ACTIVE"},
                {"Enterprise Webinar Series", "WEBINAR", "PLANNED"},
                {"Ramadan SMS Blast", "SMS", "COMPLETED"},
                {"Tech Expo Dhaka", "EVENT", "ACTIVE"},
                {"LinkedIn Lead-Gen", "SOCIAL", "ACTIVE"}
        };
        for (int i = 0; i < campaignData.length; i++) {
            String[] cm = campaignData[i];
            BigDecimal budget = BigDecimal.valueOf((i + 1) * 100000L);
            campaignRepository.save(Campaign.builder()
                    .name(cm[0]).type(CampaignType.valueOf(cm[1])).status(CampaignStatus.valueOf(cm[2]))
                    .budget(budget).revenue(budget.multiply(BigDecimal.valueOf(1.5 + i * 0.3)))
                    .startDate(LocalDate.now().minusDays(30)).endDate(LocalDate.now().plusDays(30))
                    .leadsGenerated((i + 1) * 18)
                    .description("Multi-channel campaign targeting enterprise accounts.").build());
        }

        log.info("CRM demo data seeded: {} companies, {} contacts, {} leads, {} deals.",
                companies.size(), contacts.size(), leadNames.length, dealTitles.length);
    }
}
