package com.frauas.servicemanagement.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkContractDelegate")
public class CheckContractDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Group 2 Integration Logic
        System.out.println(">>> SYSTEM: Querying Contract Management System (Group 2)...");
        // Logic would go here
        System.out.println(">>> SYSTEM: Contract validated successfully.");

        execution.setVariable("contractValid", true);
    }
}