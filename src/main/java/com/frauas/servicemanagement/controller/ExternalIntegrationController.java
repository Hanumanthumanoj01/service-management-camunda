package com.frauas.servicemanagement.controller;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/integration") // ✅ Professional URL
public class ExternalIntegrationController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    // =====================================================
    // GROUP 1: WORKFORCE INTEGRATION
    // Endpoint for Group 1 to push requirements to us
    // =====================================================
    @PostMapping("/group1/workforce-request")
    public ResponseEntity<String> receiveWorkforceNeed(@RequestBody Map<String, String> payload) {
        ServiceRequest req = new ServiceRequest();
        req.setTitle(payload.getOrDefault("title", "Incoming Request from Workforce System"));
        req.setDescription(payload.getOrDefault("description", "External Requirement Import"));
        req.setProjectContext("External Workforce Dependency");

        serviceRequestService.createServiceRequest(req);
        return ResponseEntity.ok("Request received and queued for PM review.");
    }

    // =====================================================
    // GROUP 4: CONTRACT ACKNOWLEDGEMENT
    // Endpoint for Group 4 to confirm they received the order
    // =====================================================
    @PostMapping("/group4/notify")
    public ResponseEntity<String> receiveNotification(@RequestBody Long offerId) {
        return ResponseEntity.ok(
                "Provider Management System Acknowledged: Contract preparation started for Offer ID " + offerId
        );
    }

    // =====================================================
    // GROUP 4: PROVIDER SYSTEM SIMULATION
    // =====================================================
    @PostMapping("/providers/publish-request")
    public List<ProviderOffer> getProviderResponse(@RequestBody ServiceRequest request) {

        System.out.println(">>> EXTERNAL INTEGRATION: Received broadcast for: " + request.getTitle());

        List<ProviderOffer> offers = new ArrayList<>();

        // -------- Offer 1 --------
        ProviderOffer offer1 = new ProviderOffer();
        offer1.setProviderName("Global Tech Solutions");
        offer1.setProposedRate(95.50);
        offer1.setExpertProfile("Senior DevOps Engineer, 8 yrs exp.");
        offer1.setDeliveryTimeDays(14);

        // -------- Offer 2 --------
        ProviderOffer offer2 = new ProviderOffer();
        offer2.setProviderName("Cloud Innovators GmbH");
        offer2.setProposedRate(87.25);
        offer2.setExpertProfile("Full-stack Developer, AWS Certified");
        offer2.setDeliveryTimeDays(21);

        // -------- Offer 3 --------
        ProviderOffer offer3 = new ProviderOffer();
        offer3.setProviderName("FraUAS Research Lab");
        offer3.setProposedRate(75.00);
        offer3.setExpertProfile("R&D Specialized Team");
        offer3.setDeliveryTimeDays(30);

        offers.add(offer1);
        offers.add(offer2);
        offers.add(offer3);

        return offers;
    }
}