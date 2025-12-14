package com.frauas.servicemanagement.delegate;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.entity.ServiceRequestStatus;
import com.frauas.servicemanagement.repository.ProviderOfferRepository;
import com.frauas.servicemanagement.service.EmailService;
import com.frauas.servicemanagement.service.ServiceRequestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;

@Component("publishToProvidersDelegate")
public class PublishToProvidersDelegate implements JavaDelegate {

    @Autowired private ServiceRequestService serviceRequestService;
    @Autowired private ProviderOfferRepository providerOfferRepository;
    @Autowired private EmailService emailService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long requestId = (Long) execution.getVariable("requestId");
        String rpUser = "rp_user"; // Hardcode Resource Planner for email routing

        Optional<ServiceRequest> requestOpt = serviceRequestService.getServiceRequestById(requestId);

        if (requestOpt.isPresent()) {
            ServiceRequest request = requestOpt.get();
            RestTemplate restTemplate = new RestTemplate();

            // CORRECT PATH: Calls our own Mock Integration Controller
            String group4Url = "http://localhost:8081/mock-api/providers/publish-request";

            try {
                // REST API Call (Automatic Offers)
                ProviderOffer[] offers = restTemplate.postForObject(group4Url, request, ProviderOffer[].class);

                if (offers != null && offers.length > 0) {
                    for (ProviderOffer offer : Arrays.asList(offers)) {
                        offer.setServiceRequest(request);
                        providerOfferRepository.save(offer);
                    }

                    // CRITICAL: Update status so RP sees the task and offers
                    serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.OFFERS_RECEIVED);

                    // Email Notification to Resource Planner (Test 1)
                    emailService.sendNotification(
                            rpUser,
                            "ACTION REQUIRED: New Offers for Request " + requestId,
                            offers.length + " new provider offers are ready for evaluation."
                    );
                    System.out.println(">>> SUCCESS: Offers received, RP Notified.");
                } else {
                    serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.PUBLISHED);
                }

            } catch (Exception e) {
                System.err.println("!!! AUTO-OFFER FAILED. Creating fallback offer. Error: " + e.getMessage());
                // Fail-safe: Create one fallback offer if the API call crashes
                ProviderOffer fallback = new ProviderOffer();
                fallback.setProviderName("API Down - Fallback");
                fallback.setProposedRate(99.99);
                fallback.setExpertProfile("Fallback Consultant");
                fallback.setDeliveryTimeDays(20);
                fallback.setServiceRequest(request);
                providerOfferRepository.save(fallback);

                serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.OFFERS_RECEIVED);
            }
        }
    }
}