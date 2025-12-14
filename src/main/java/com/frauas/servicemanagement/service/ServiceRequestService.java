package com.frauas.servicemanagement.service;

import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.entity.ServiceRequestStatus;
import com.frauas.servicemanagement.repository.ServiceRequestRepository;
import com.frauas.servicemanagement.repository.ProviderOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ProviderOfferRepository providerOfferRepository; // ✅ CRITICAL FIX

    @Autowired
    private CamundaProcessService camundaProcessService;

    /**
     * Return all service requests ordered by createdAt descending.
     */
    @Transactional(readOnly = true)
    public List<ServiceRequest> getAllServiceRequests() {
        return serviceRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<ServiceRequest> getServiceRequestById(Long id) {
        return serviceRequestRepository.findById(id);
    }

    /**
     * Create a new ServiceRequest and populate mocked external references.
     * This method saves a DRAFT/basic record.
     */
    public ServiceRequest createServiceRequest(ServiceRequest serviceRequest) {

        // Mock: Simulate dependency on internal workforce tool
        serviceRequest.setInternalRequestId(1000L + (long) (Math.random() * 1000));

        // Mock: Simulate dependency on contract management
        serviceRequest.setContractId(2000L + (long) (Math.random() * 1000));

        return serviceRequestRepository.save(serviceRequest);
    }

    /**
     * Creates a service request and immediately starts the Camunda process.
     */
    public ServiceRequest createServiceRequestWithProcess(ServiceRequest serviceRequest) {

        ServiceRequest savedRequest = createServiceRequest(serviceRequest);

        String processInstanceId = camundaProcessService.startServiceRequestProcess(
                savedRequest.getId(),
                savedRequest.getTitle(),
                savedRequest.getDescription()
        );

        savedRequest.setStatus(ServiceRequestStatus.WAITING_APPROVAL);
        serviceRequestRepository.save(savedRequest);

        System.out.println(
                "Started Camunda process: " + processInstanceId +
                        " for Request ID: " + savedRequest.getId()
        );

        return savedRequest;
    }

    /**
     * Update status of an existing ServiceRequest.
     */
    public ServiceRequest updateServiceRequestStatus(Long id, ServiceRequestStatus status) {

        Optional<ServiceRequest> optionalRequest = serviceRequestRepository.findById(id);
        if (optionalRequest.isPresent()) {
            ServiceRequest request = optionalRequest.get();
            request.setStatus(status);
            return serviceRequestRepository.save(request);
        }
        return null;
    }

    /**
     * ✅ CRITICAL FIX
     * Handles rejection: updates status AND stores rejection reason.
     */
    public ServiceRequest rejectServiceRequest(Long id, String reason) {

        Optional<ServiceRequest> optionalRequest = serviceRequestRepository.findById(id);
        if (optionalRequest.isPresent()) {

            ServiceRequest request = optionalRequest.get();
            request.setStatus(ServiceRequestStatus.REJECTED);
            request.setRejectionReason(reason);

            return serviceRequestRepository.save(request);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<ServiceRequest> getServiceRequestsByStatus(ServiceRequestStatus status) {
        return serviceRequestRepository.findByStatus(status);
    }

    /**
     * ✅ CRITICAL FIX
     * Handles deletion safely by removing child entities first
     * to avoid foreign key constraint violations.
     */
    public void deleteServiceRequest(Long id) {

        // 1. Ensure parent exists
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Service Request not found with ID: " + id));

        // 2. Delete child entities first (Provider Offers)
        providerOfferRepository.deleteByServiceRequest(request);

        // 3. Delete parent entity
        serviceRequestRepository.delete(request);
    }
}
