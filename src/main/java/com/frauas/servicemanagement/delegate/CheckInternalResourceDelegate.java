package com.frauas.servicemanagement.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkInternalResourceDelegate")
public class CheckInternalResourceDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long internalRequestId = (Long) execution.getVariable("requestId");
        // Mock Logic: Always return "false" (No internal resource found) so workflow continues
        boolean internalResourceAvailable = false;

        System.out.println(">>> MOCK (Group 1): Checking Internal Workforce for Request ID " + internalRequestId);
        System.out.println(">>> MOCK (Group 1): No internal resources available. Proceeding to external procurement.");

        execution.setVariable("internalResourceAvailable", internalResourceAvailable);
    }
}