package com.frauas.servicemanagement.delegate;

import com.frauas.servicemanagement.entity.ServiceRequestStatus;
import com.frauas.servicemanagement.service.ServiceRequestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("notifyProviderRejectDelegate")
public class NotifyProviderRejectDelegate implements JavaDelegate {

    @Autowired private ServiceRequestService serviceRequestService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long requestId = (Long) execution.getVariable("requestId");
        Long offerId = (Long) execution.getVariable("selectedOfferId");

        serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.REJECTED);

        System.out.println("\n>>> [API OUT] GROUP 3B -> GROUP 4B: Rejection Notification");
        System.out.println("   REQUEST ID: " + requestId);
        System.out.println("   OFFER ID: " + offerId);
        System.out.println("   STATUS: NOT_SELECTED");
        System.out.println("   REASON: Rejected by Client Manager (1b)");
        System.out.println("=========================================================\n");
    }
}