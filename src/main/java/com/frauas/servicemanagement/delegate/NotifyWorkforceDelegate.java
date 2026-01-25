package com.frauas.servicemanagement.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.repository.ProviderOfferRepository;
import com.frauas.servicemanagement.service.ServiceRequestService;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component("notifyWorkforceDelegate")
public class NotifyWorkforceDelegate implements JavaDelegate {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ProviderOfferRepository offerRepo;

    // Use RestTemplate to send HTTP requests
    private final RestTemplate restTemplate = new RestTemplate();
    // Use ObjectMapper to create clean JSON strings
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        Long reqId = (Long) execution.getVariable("requestId");
        Long offerId = (Long) execution.getVariable("selectedOfferId");

        if (reqId == null || offerId == null) {
            throw new IllegalStateException("Missing process variables: requestId or selectedOfferId");
        }

        ServiceRequest req = serviceRequestService.getServiceRequestById(reqId)
                .orElseThrow(() -> new IllegalStateException("ServiceRequest not found: " + reqId));

        ProviderOffer offer = offerRepo.findById(offerId)
                .orElseThrow(() -> new IllegalStateException("ProviderOffer not found: " + offerId));

        // --------------------------------------------------
        // 1. Format Skills (Clean String: "Java, Python")
        // --------------------------------------------------
        String cleanSkills = "Unknown";
        if (offer.getSkills() != null) {
            // Remove brackets [] if they exist and trim whitespace
            cleanSkills = offer.getSkills()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .trim();
        }

        // --------------------------------------------------
        // 2. Get Location (From Request)
        // --------------------------------------------------
        // Note: We use the location from the ServiceRequest because
        // that matches what 1b originally asked for.
        String location = req.getPerformanceLocation();
        if (location == null || location.isEmpty()) {
            location = "Remote"; // Default fallback
        }

        String contractId = offer.getContractId() != null ? offer.getContractId() : req.getContractId();

        // --------------------------------------------------
        // 3. Build the Payload Object (Exact 1b Format)
        // --------------------------------------------------
        Map<String, Object> payload = new HashMap<>();
        payload.put("staffingRequestId", req.getInternalRequestId());
        payload.put("externalEmployeeId", offer.getExternalOfferId());
        payload.put("provider", offer.getProviderName());
        payload.put("firstName", offer.getFirstName());
        payload.put("skills", cleanSkills); // Value: "java,python"
        payload.put("location", location);  // Value:  or "Berlin"
        payload.put("lastName", offer.getLastName());
        payload.put("email", offer.getEmail());
        payload.put("contractId", contractId);
        payload.put("evaluationScore", offer.getTotalScore());
        payload.put("wagePerHour", offer.getHourlyRate());
        payload.put("projectId", req.getInternalProjectId());

        // REMOVED: payload.put("status", "OFFER_WON");
        // REMOVED: payload.put("experienceYears", ...); (Not in your requested list, removed to be safe)

        // --------------------------------------------------
        // 4. LOGGING (Console)
        // --------------------------------------------------
        String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);

        System.out.println("\n>>> [API OUT] GROUP 3B -> GROUP 1B: Recommendation Sent");
        System.out.println("   ENDPOINT: POST https://workforce-planning-tool.onrender.com/api/group3b/workforce-response");
        System.out.println("   PAYLOAD: " + jsonString);
        System.out.println("=========================================================\n");

        // --------------------------------------------------
        // 5. SEND REAL REQUEST
        // --------------------------------------------------
        String targetUrl = "https://workforce-planning-tool.onrender.com/api/group3b/workforce-response";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

            restTemplate.postForObject(targetUrl, requestEntity, String.class);

            System.out.println(">>> SUCCESS: Real notification successfully sent to Group 1b API.");

        } catch (Exception e) {
            System.err.println("!!! WARNING: Failed to send real notification to Group 1b. Their server might be down.");
            System.err.println("!!! Error Details: " + e.getMessage());
        }
    }
}