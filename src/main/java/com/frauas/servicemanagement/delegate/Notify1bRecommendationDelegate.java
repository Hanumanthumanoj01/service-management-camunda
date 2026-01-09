package com.frauas.servicemanagement.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("notify1bRecommendationDelegate")
public class Notify1bRecommendationDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long requestId = (Long) execution.getVariable("requestId");
        Long offerId = (Long) execution.getVariable("selectedOfferId");

        System.out.println("\n>>> [API OUT] GROUP 3B -> GROUP 1B: Sending Recommendation");
        System.out.println("   REQUEST ID: " + requestId);
        System.out.println("   OFFER ID: " + offerId);
        System.out.println("   STATUS: PENDING_MANAGER_APPROVAL");
        System.out.println("   (System is now WAITING for 1b callback via /api/integration/group1/decision...)");
        System.out.println("=========================================================\n");
    }
}