package com.frauas.servicemanagement.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkInternalResourceDelegate")
public class CheckInternalResourceDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long internalRequestId = (Long) execution.getVariable("requestId");
        boolean internalResourceAvailable = false;

        System.out.println(">>> SYSTEM: Checking Internal Workforce Database (Group 1) for Request ID " + internalRequestId);
        System.out.println(">>> SYSTEM: No internal resources available. Proceeding to external procurement.");

        execution.setVariable("internalResourceAvailable", internalResourceAvailable);
    }
}