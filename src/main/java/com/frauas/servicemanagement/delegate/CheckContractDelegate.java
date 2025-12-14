package com.frauas.servicemanagement.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkContractDelegate")
public class CheckContractDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Mock Logic: Always validate successfully
        System.out.println(">>> MOCK (Group 2): Validating Contract Details...");
        System.out.println(">>> MOCK (Group 2): Contract is VALID.");

        execution.setVariable("contractValid", true);
    }
}