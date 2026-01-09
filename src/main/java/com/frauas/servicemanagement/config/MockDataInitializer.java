//package com.frauas.servicemanagement.config;
//
//import com.frauas.servicemanagement.entity.*;
//import com.frauas.servicemanagement.repository.ProviderOfferRepository;
//import com.frauas.servicemanagement.repository.ServiceRequestRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//
//@Component
//public class MockDataInitializer implements CommandLineRunner {
//
//    @Autowired private ServiceRequestRepository requestRepo;
//    @Autowired private ProviderOfferRepository offerRepo;
//
//    @Override
//    public void run(String... args) throws Exception {
//        if (requestRepo.count() > 0) return; // Don't overwrite if data exists
//
//        System.out.println(">>> [INIT] Generating Mock Data for Group 5b Reporting...");
//        Random rand = new Random();
//
//        // --- 1. CREATE COMPLETED REQUESTS (Historical Data) ---
//        createRequestWithOffers("Cloud Migration Alpha", "AWS", 5, 1200.0, ServiceRequestStatus.COMPLETED, true);
//        createRequestWithOffers("Legacy Java Update", "Java 8 to 17", 3, 800.0, ServiceRequestStatus.COMPLETED, true);
//        createRequestWithOffers("SAP Interface Setup", "ABAP", 8, 1500.0, ServiceRequestStatus.COMPLETED, true);
//        createRequestWithOffers("Frontend React Rewrite", "React/Redux", 4, 950.0, ServiceRequestStatus.COMPLETED, true);
//        createRequestWithOffers("Cybersecurity Audit", "Security", 10, 2000.0, ServiceRequestStatus.COMPLETED, true);
//
//        // --- 2. CREATE ACTIVE REQUESTS (In Progress) ---
//        createRequestWithOffers("AI Chatbot Prototype", "Python/LLM", 2, 1100.0, ServiceRequestStatus.OFFERS_RECEIVED, false);
//        createRequestWithOffers("DevOps Pipeline Fix", "Jenkins/Docker", 4, 900.0, ServiceRequestStatus.OFFERS_RECEIVED, false);
//        createRequestWithOffers("Mobile App MVP", "Flutter", 3, 850.0, ServiceRequestStatus.PUBLISHED, false);
//        createRequestWithOffers("Data Warehouse Setup", "SQL/Snowflake", 6, 1300.0, ServiceRequestStatus.WAITING_APPROVAL, false);
//        createRequestWithOffers("ERP Integration", "Oracle", 7, 1400.0, ServiceRequestStatus.WAITING_APPROVAL, false);
//        createRequestWithOffers("Network Upgrade", "Cisco", 5, 1000.0, ServiceRequestStatus.WAITING_APPROVAL, false);
//
//        // --- 3. CREATE DRAFTS ---
//        createRequestWithOffers("HR System Review", "Consulting", 2, 600.0, ServiceRequestStatus.DRAFT, false);
//        createRequestWithOffers("Office 365 Rollout", "Admin", 1, 500.0, ServiceRequestStatus.DRAFT, false);
//    }
//
//    private void createRequestWithOffers(String title, String skills, int exp, double budget, ServiceRequestStatus status, boolean hasWinner) {
//        ServiceRequest req = new ServiceRequest();
//        req.setTitle(title);
//        req.setDescription("Automatic mock request for reporting testing.");
//        req.setRequiredSkills(skills);
//        req.setMinExperience(exp);
//        req.setMaxDailyRate(budget);
//        req.setDurationDays(30 + new Random().nextInt(90));
//        req.setStatus(status);
//        req.setProjectContext("Project-" + new Random().nextInt(100));
//        req.setCreatedAt(LocalDateTime.now().minusDays(new Random().nextInt(60)));
//        requestRepo.save(req);
//
//        // Generate Random Offers for this request
//        if (status != ServiceRequestStatus.DRAFT && status != ServiceRequestStatus.WAITING_APPROVAL) {
//            int offerCount = 2 + new Random().nextInt(3); // 2 to 4 offers
//            String[] providers = {"Global Tech", "Check24", "Siemens", "T-Systems", "Infosys"};
//
//            for (int i = 0; i < offerCount; i++) {
//                ProviderOffer offer = new ProviderOffer();
//                offer.setServiceRequest(req);
//                offer.setProviderName(providers[new Random().nextInt(providers.length)]);
//                offer.setSpecialistName("Expert " + (char)('A' + new Random().nextInt(26)));
//                offer.setDailyRate(budget * (0.8 + (new Random().nextDouble() * 0.4))); // +/- budget
//                offer.setTotalCost(offer.getDailyRate() * req.getDurationDays());
//                offer.setSubmittedAt(LocalDateTime.now().minusDays(new Random().nextInt(10)));
//                offer.setTechnicalScore((double) (50 + new Random().nextInt(50)));
//                offer.setStatus(OfferStatus.SUBMITTED);
//
//                // If this request is COMPLETED, mark one offer as SELECTED
//                if (hasWinner && i == 0) {
//                    offer.setStatus(OfferStatus.SELECTED);
//                    offer.setProviderName("Global Tech"); // Skew data for "Global Tech" winning
//                }
//
//                offerRepo.save(offer);
//            }
//        }
//    }
//}