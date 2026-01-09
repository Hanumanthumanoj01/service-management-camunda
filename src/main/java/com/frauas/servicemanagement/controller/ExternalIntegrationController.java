package com.frauas.servicemanagement.controller;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.service.CamundaProcessService;
import com.frauas.servicemanagement.service.ProviderOfferService;
import com.frauas.servicemanagement.service.ServiceRequestService;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * External Integration Controller
 * Handles communication with other project groups
 */
@RestController
@RequestMapping("/api/integration")
public class ExternalIntegrationController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ProviderOfferService providerOfferService;

    @Autowired
    private CamundaProcessService camundaProcessService;

    @Autowired
    private RuntimeService runtimeService;

    // =====================================================
    // GROUP 1: WORKFORCE INTEGRATION (1b Request)
    // Receives workforce requirement and starts workflow
    // =====================================================
    @PostMapping("/group1/workforce-request")
    public ResponseEntity<String> receiveWorkforceNeed(@RequestBody Map<String, Object> payload) {
        ServiceRequest request = new ServiceRequest();

        // Map fields safely
        request.setTitle((String) payload.getOrDefault("jobTitle", "Workforce Request (Needs Review)"));
        request.setDescription((String) payload.getOrDefault("description", "External request from Group 1b"));
        request.setProjectContext((String) payload.getOrDefault("project", "Unknown Project"));

        // Handle numbers safely
        if (payload.get("internalRequestId") != null) {
            request.setInternalRequestId(Long.valueOf(payload.get("internalRequestId").toString()));
        }

        // Save as DRAFT so PM sees it in "My Drafts"
        serviceRequestService.createServiceRequest(request);

        // ❌ REMOVED: serviceRequestService.startProcessForRequest(request.getId());
        // ✅ REASON: PM must review first.

        return ResponseEntity.ok("Request received. PM will review and initiate process.");
    }

    // =====================================================
    // GROUP 1B: DECISION CALLBACK (NEW)
    // Accept / Reject recommendation to unblock BPMN
    // =====================================================
    @PostMapping("/group1/decision")
    public ResponseEntity<String> receive1bDecision(
            @RequestBody Map<String, Object> payload) {

        Long requestId =
                Long.valueOf(payload.get("requestId").toString());
        String decision =
                payload.get("decision").toString(); // ACCEPTED / REJECTED

        System.out.println(
                ">>> [API IN] GROUP 1B DECISION: "
                        + decision + " for Request ID " + requestId);

        Execution execution = runtimeService.createExecutionQuery()
                .processVariableValueEquals("requestId", requestId)
                .messageEventSubscriptionName("Message_1b_Decision")
                .singleResult();

        if (execution == null) {
            return ResponseEntity.badRequest().body(
                    "No active process waiting for 1b decision for Request ID " + requestId);
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("oneBDecision", decision);

        runtimeService.createMessageCorrelation("Message_1b_Decision")
                .processInstanceId(execution.getProcessInstanceId())
                .setVariables(variables)
                .correlate();

        return ResponseEntity.ok(
                "Decision received and process unblocked successfully.");
    }

    // =====================================================
    // GROUP 4: CONTRACT ACKNOWLEDGEMENT
    // Confirms contract preparation has started
    // =====================================================
    @PostMapping("/group4/notify")
    public ResponseEntity<String> receiveNotification(
            @RequestBody Long offerId) {

        return ResponseEntity.ok(
                "Provider Management System acknowledged. "
                        + "Contract preparation started for Offer ID: " + offerId);
    }

    // =====================================================
    // GROUP 4: PROVIDER OFFER (REAL INTEGRATION)
    // Accepts Group 4 JSON structure
    // =====================================================
    @PostMapping("/group4/offer")
    public ResponseEntity<String> receiveProviderOffer(
            @RequestBody Map<String, Object> payload) {

        Long serviceRequestId =
                Long.valueOf(payload.get("serviceRequestId").toString());

        ProviderOffer offer = new ProviderOffer();

        offer.setExternalOfferId(payload.get("offerId").toString());
        offer.setProviderName(payload.get("company").toString());
        offer.setServiceType(payload.get("serviceType").toString());
        offer.setSpecialistName(payload.get("specialistName").toString());
        offer.setDailyRate(Double.valueOf(payload.get("dailyRate").toString()));
        offer.setOnsiteDays(Integer.valueOf(payload.get("onsiteDays").toString()));
        offer.setTravelCost(Double.valueOf(payload.get("travellingCost").toString()));
        offer.setTotalCost(Double.valueOf(payload.get("totalCost").toString()));
        offer.setContractType(payload.get("contractualRelationship").toString());
        offer.setSkills(payload.get("skills").toString());

        providerOfferService.submitOffer(serviceRequestId, offer);

        return ResponseEntity.ok(
                "Offer received successfully and queued for evaluation.");
    }

    // =====================================================
    // GROUP 4: PROVIDER SYSTEM SIMULATION (MOCK)
    // Used when Group 4 is unavailable
    // =====================================================
    @PostMapping("/providers/publish-request")
    public List<ProviderOffer> simulateProviderResponses(
            @RequestBody ServiceRequest request) {

        System.out.println(
                ">>> EXTERNAL INTEGRATION: Broadcasting request: "
                        + request.getTitle());

        List<ProviderOffer> offers = new ArrayList<>();

        ProviderOffer offer1 = new ProviderOffer();
        offer1.setProviderName("Global Tech Solutions");
        offer1.setDailyRate(95.50);
        offer1.setSpecialistName("Senior DevOps Engineer");
        offer1.setSkills("Docker, Kubernetes, CI/CD");
        offer1.setOnsiteDays(10);
        offer1.setTravelCost(1200.00);
        offer1.setTotalCost(9500.00);
        offer1.setContractType("Time & Material");

        ProviderOffer offer2 = new ProviderOffer();
        offer2.setProviderName("Cloud Innovators GmbH");
        offer2.setDailyRate(87.25);
        offer2.setSpecialistName("Full-stack Developer");
        offer2.setSkills("Java, Spring Boot, AWS");
        offer2.setOnsiteDays(5);
        offer2.setTravelCost(800.00);
        offer2.setTotalCost(8200.00);
        offer2.setContractType("Fixed Price");

        ProviderOffer offer3 = new ProviderOffer();
        offer3.setProviderName("FraUAS Research Lab");
        offer3.setDailyRate(75.00);
        offer3.setSpecialistName("R&D Specialist Team");
        offer3.setSkills("AI Research, Prototyping");
        offer3.setOnsiteDays(0);
        offer3.setTravelCost(0.00);
        offer3.setTotalCost(7000.00);
        offer3.setContractType("Research Contract");

        offers.add(offer1);
        offers.add(offer2);
        offers.add(offer3);

        return offers;
    }
}
