package com.frauas.servicemanagement.controller;

import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.entity.ServiceRequestStatus;
import com.frauas.servicemanagement.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    // -----------------------
    // Page endpoints (Thymeleaf)
    // -----------------------

    @GetMapping
    public String getAllServiceRequests(Model model) {
        List<ServiceRequest> requests = serviceRequestService.getAllServiceRequests();
        model.addAttribute("serviceRequests", requests);
        model.addAttribute("newRequest", new ServiceRequest());
        return "service-requests";
    }

    /**
     * Creates a new ServiceRequest and immediately starts the Camunda workflow.
     */
    @PostMapping
    public String createServiceRequest(@ModelAttribute ServiceRequest serviceRequest) {
        serviceRequestService.createServiceRequestWithProcess(serviceRequest);
        return "redirect:/service-requests";
    }

    /**
     * Manually start the Camunda process for a ServiceRequest that is currently in DRAFT status.
     */
    @PostMapping("/{id}/process")
    public String startProcessForDraft(@PathVariable Long id) {
        Optional<ServiceRequest> maybe = serviceRequestService.getServiceRequestById(id);
        if (maybe.isPresent()) {
            ServiceRequest request = maybe.get();
            if (request.getStatus() == ServiceRequestStatus.DRAFT) {
                serviceRequestService.createServiceRequestWithProcess(request);
            }
        }
        return "redirect:/service-requests";
    }

    /**
     * Update the status of a service request (form posts enum name).
     */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam ServiceRequestStatus status) {
        serviceRequestService.updateServiceRequestStatus(id, status);
        return "redirect:/service-requests";
    }

    @GetMapping("/{id}")
    public String getServiceRequestDetails(@PathVariable Long id, Model model) {
        ServiceRequest request = serviceRequestService.getServiceRequestById(id).orElse(null);
        model.addAttribute("serviceRequest", request);
        return "service-request-details";
    }

    /**
     * Delete a service request (simple demo endpoint).
     */
    @PostMapping("/{id}/delete")
    public String deleteRequest(@PathVariable Long id) {
        serviceRequestService.deleteServiceRequest(id);
        return "redirect:/service-requests";
    }

    // -----------------------
    // Lightweight REST endpoints (for other services/consumers)
    // -----------------------

    @GetMapping("/api")
    @ResponseBody
    public List<ServiceRequest> getAllServiceRequestsApi() {
        return serviceRequestService.getAllServiceRequests();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ServiceRequest getServiceRequestApi(@PathVariable Long id) {
        return serviceRequestService.getServiceRequestById(id).orElse(null);
    }
}
