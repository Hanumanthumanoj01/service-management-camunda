package com.frauas.servicemanagement.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("notifyProviderDelegate")
public class NotifyProviderDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long offerId = (Long) execution.getVariable("selectedOfferId");
        System.out.println(">>> NOTIFICATION DELEGATE: Informing Group 4 about Offer Acceptance (ID: " + offerId + ")");

        // Call the Mock API to simulate Group 4 receiving the news
        RestTemplate rest = new RestTemplate();
        try {
            String response = rest.postForObject("http://localhost:8081/mock-api/group4/notify", offerId, String.class);
            System.out.println(">>> GROUP 4 RESPONSE: " + response);
        } catch (Exception e) {
            System.err.println(">>> MOCK API WARNING: Could not reach Group 4 mock endpoint.");
        }
    }
}