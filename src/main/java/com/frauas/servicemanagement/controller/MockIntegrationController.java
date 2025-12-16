//package com.frauas.servicemanagement.controller;
//
//import com.frauas.servicemanagement.entity.ProviderOffer;
//import com.frauas.servicemanagement.entity.ServiceRequest;
//import com.frauas.servicemanagement.service.ServiceRequestService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//@RestController
//@RequestMapping("/mock-api")
//public class MockIntegrationController {
//
//    @Autowired
//    private ServiceRequestService serviceRequestService;
//
//    // =====================================================
//    // GROUP 1 SIMULATION
//    // Simulates Group 1 sending a workforce requirement
//    // =====================================================
//    @PostMapping("/group1/workforce-request")
//    public ResponseEntity<String> receiveWorkforceNeed(@RequestBody Map<String, String> payload) {
//        ServiceRequest req = new ServiceRequest();
//        req.setTitle(payload.getOrDefault("title", "Incoming Request from Group 1"));
//        req.setDescription(payload.getOrDefault("description", "Imported Requirement"));
//        req.setProjectContext("External Workforce Dependency");
//
//        //  Add default values so the UI doesn't look broken
////        req.setDurationDays(30);
////        req.setRequiredSkills("Java, Spring Boot, Camunda");
//
//        serviceRequestService.createServiceRequest(req);
//        return ResponseEntity.ok("Request received successfully.");
//    }
//
//    // =====================================================
//    // GROUP 4 ACKNOWLEDGEMENT SIMULATION
//    // Simulates Group 4 acknowledging selected offer
//    // =====================================================
//    @PostMapping("/group4/notify")
//    public ResponseEntity<String> receiveNotification(@RequestBody Long offerId) {
//        return ResponseEntity.ok(
//                "Group 4 Acknowledged: We will prepare the contract for Offer " + offerId
//        );
//    }
//
//    // =====================================================
//    // GROUP 4 PROVIDER MOCK ENDPOINT (IMPORTANT)
//    // THIS IS WHAT YOUR BPMN DELEGATE CALLS
//    // =====================================================
//    @PostMapping("/providers/publish-request")
//    public List<ProviderOffer> mockProviderResponse(@RequestBody ServiceRequest request) {
//
//        System.out.println(">>> MOCK GROUP 4: Received request for: " + request.getTitle());
//
//        List<ProviderOffer> mockOffers = new ArrayList<>();
//
//        // -------- Offer 1 --------
//        ProviderOffer offer1 = new ProviderOffer();
//        offer1.setProviderName("Global Tech Solutions");
//        offer1.setProposedRate(95.50);
//        offer1.setExpertProfile("Senior DevOps Engineer with 8 years experience");
//        offer1.setDeliveryTimeDays(14);
//
//        // -------- Offer 2 --------
//        ProviderOffer offer2 = new ProviderOffer();
//        offer2.setProviderName("Cloud Innovators GmbH");
//        offer2.setProposedRate(87.25);
//        offer2.setExpertProfile("Full-stack Developer, AWS Certified");
//        offer2.setDeliveryTimeDays(21);
//
//        // -------- Offer 3 --------
//        ProviderOffer offer3 = new ProviderOffer();
//        offer3.setProviderName("FraUAS Internal Team");
//        offer3.setProposedRate(75.00);
//        offer3.setExpertProfile("University Research & Development Team");
//        offer3.setDeliveryTimeDays(30);
//
//        mockOffers.add(offer1);
//        mockOffers.add(offer2);
//        mockOffers.add(offer3);
//
//        System.out.println(">>> MOCK GROUP 4: Returning " + mockOffers.size() + " offers");
//
//        return mockOffers;
//    }
//}
