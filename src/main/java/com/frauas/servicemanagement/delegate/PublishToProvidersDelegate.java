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

import java.time.LocalDateTime;
import java.util.Optional;

@Component("publishToProvidersDelegate")
public class PublishToProvidersDelegate implements JavaDelegate {

    @Autowired private ServiceRequestService serviceRequestService;
    @Autowired private ProviderOfferRepository providerOfferRepository;
    @Autowired private EmailService emailService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long requestId = (Long) execution.getVariable("requestId");
        System.out.println(">>> SYSTEM: Publishing Request ID: " + requestId + " to External Providers...");

        Optional<ServiceRequest> requestOpt = serviceRequestService.getServiceRequestById(requestId);

        if (requestOpt.isPresent()) {
            ServiceRequest request = requestOpt.get();

            // 1. Update Status
            serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.PUBLISHED);

            // 2. SIMULATE EXTERNAL RESPONSE (INTERNAL LOGIC for Speed)
            try {
                // Offer 1
                ProviderOffer o1 = new ProviderOffer(request, "Global Tech Solutions", 120.00, "Senior Java Dev", 10);
                o1.setSubmittedAt(LocalDateTime.now());
                providerOfferRepository.save(o1);

                // Offer 2
                ProviderOffer o2 = new ProviderOffer(request, "Cloud Innovators GmbH", 95.50, "AWS Expert", 15);
                o2.setSubmittedAt(LocalDateTime.now());
                providerOfferRepository.save(o2);

                System.out.println(">>> INTEGRATION SUCCESS: 2 Provider Offers received via API.");

                // 3. Update Status
                serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.OFFERS_RECEIVED);

                // 4. Send Email
                emailService.sendNotification(
                        "rp_user",
                        "ACTION REQUIRED: Offers Received for Request " + requestId,
                        "External offers have been received and are ready for evaluation."
                );

            } catch (Exception e) {
                System.err.println("!!! INTEGRATION ERROR: " + e.getMessage());
            }
        }
    }
}