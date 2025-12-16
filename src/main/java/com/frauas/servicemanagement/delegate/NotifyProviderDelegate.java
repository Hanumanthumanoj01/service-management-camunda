package com.frauas.servicemanagement.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("notifyProviderDelegate")
public class NotifyProviderDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long offerId = (Long) execution.getVariable("selectedOfferId");

        // DIRECT SIMULATION (Instant)
        System.out.println(">>> NOTIFICATION DELEGATE: Informing Group 4 about Offer Acceptance (ID: " + offerId + ")");
        System.out.println(">>> GROUP 4 RESPONSE : Acknowledged. Contract preparation started.");
    }
}