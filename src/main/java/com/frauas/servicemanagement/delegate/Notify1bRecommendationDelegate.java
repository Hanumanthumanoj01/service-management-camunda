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

@Component("notify1bRecommendationDelegate")
public class Notify1bRecommendationDelegate implements JavaDelegate {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ProviderOfferRepository offerRepo;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        Long reqId = (Long) execution.getVariable("requestId");
        Long offerId = (Long) execution.getVariable("selectedOfferId");

        ServiceRequest req = serviceRequestService.getServiceRequestById(reqId).orElseThrow();
        ProviderOffer offer = offerRepo.findById(offerId).orElseThrow();

        // 1. SKILLS CLEANUP
        String cleanSkills = "Unknown";
        if (offer.getSkills() != null) {
            cleanSkills = offer.getSkills().replace("[", "").replace("]", "").replace("\"", "").trim();
        }

        // 2. LOCATION LOGIC (PRIORITIZE OFFER, FALLBACK TO REQUEST)
        String finalLocation = offer.getLocation(); // Try Offer first (from 4b)
        if (finalLocation == null || finalLocation.isEmpty()) {
            finalLocation = req.getPerformanceLocation(); // Fallback to Request
        }
        if (finalLocation == null || finalLocation.isEmpty()) {
            finalLocation = "Frankfurt"; // Safety Fallback
        }

        // 3. CONTRACT ID
        String contractId = offer.getContractId();
        if (contractId == null || contractId.isEmpty()) contractId = req.getContractId();
        if (contractId == null) contractId = "CTR-PENDING";

        // 4. BUILD PAYLOAD
        Map<String, Object> payload = new HashMap<>();
        payload.put("staffingRequestId", req.getInternalRequestId());
        payload.put("externalEmployeeId", offer.getExternalOfferId());
        payload.put("provider", offer.getProviderName());
        payload.put("firstName", offer.getFirstName());
        payload.put("lastName", offer.getLastName());
        payload.put("email", offer.getEmail());
        payload.put("wagePerHour", offer.getHourlyRate());

        payload.put("skills", cleanSkills);
        payload.put("location", finalLocation); // ✅ NOW USES 4B VALUE
        payload.put("experienceYears", offer.getExperienceYears());
        payload.put("contractId", contractId);
        payload.put("evaluationScore", offer.getTotalScore());
        payload.put("projectId", req.getInternalProjectId());

        // 5. SEND & LOG
        String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
        System.out.println("\n>>> [API OUT] GROUP 3B -> GROUP 1B: Recommendation Sent");
        System.out.println("   PAYLOAD: " + jsonString);

        String targetUrl = "https://workforce-planning-tool.onrender.com/api/group3b/workforce-response";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(targetUrl, requestEntity, String.class);
            System.out.println(">>> SUCCESS: Real notification sent to Group 1b.");
        } catch (Exception e) {
            System.err.println("!!! WARNING: Failed to send to 1b: " + e.getMessage());
        }
    }
}