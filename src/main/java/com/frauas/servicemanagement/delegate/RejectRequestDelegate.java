package com.frauas.servicemanagement.delegate;

import com.frauas.servicemanagement.service.EmailService;
import com.frauas.servicemanagement.service.ServiceRequestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("rejectRequestDelegate")
public class RejectRequestDelegate implements JavaDelegate {

    @Autowired private ServiceRequestService serviceRequestService;
    @Autowired private EmailService emailService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long requestId = (Long) execution.getVariable("requestId");
        String reason = (String) execution.getVariable("rejectionReason");
        String pmUser = (String) execution.getVariable("projectManager");

        // 1. CRITICAL FIX: Persist status and reason to DB
        serviceRequestService.rejectServiceRequest(requestId, reason);

        // 2. Email Notification (Test 1)
        emailService.sendNotification(
                pmUser,
                "Request " + requestId + " REJECTED",
                "Your service request was rejected by the Procurement Officer.\nReason: " + reason +
                        "\nPlease log in to revise and resubmit."
        );

        System.out.println(">>> PROCESS REJECTED: Request ID " + requestId + ". PM Notified.");
    }
}