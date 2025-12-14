package com.frauas.servicemanagement.delegate;

import com.frauas.servicemanagement.entity.ServiceRequestStatus;
import com.frauas.servicemanagement.service.EmailService;
import com.frauas.servicemanagement.service.ServiceRequestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Delegate to update the Service Request status to COMPLETED
 */
@Component("finalizeRequestDelegate")
public class FinalizeRequestDelegate implements JavaDelegate {

    @Autowired private ServiceRequestService serviceRequestService;
    @Autowired private EmailService emailService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long requestId = (Long) execution.getVariable("requestId");
        String pmUser = (String) execution.getVariable("projectManager");

        serviceRequestService.updateServiceRequestStatus(requestId, ServiceRequestStatus.COMPLETED);

        // Email Notification (Test 1)
        emailService.sendNotification(
                pmUser,
                "Request " + requestId + " - FINALIZED & COMPLETED",
                "Your service request has been successfully completed."
        );

        System.out.println("Delegate executed: Service Request ID " + requestId + " successfully COMPLETED.");
    }
}