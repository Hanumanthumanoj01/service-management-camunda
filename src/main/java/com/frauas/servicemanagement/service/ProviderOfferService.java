package com.frauas.servicemanagement.service;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.repository.ProviderOfferRepository;
import com.frauas.servicemanagement.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProviderOfferService {

    @Autowired
    private ProviderOfferRepository providerOfferRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Transactional(readOnly = true)
    public List<ProviderOffer> getOffersByServiceRequest(Long serviceRequestId) {
        return providerOfferRepository.findByServiceRequestId(serviceRequestId);
    }

    public ProviderOffer submitOffer(Long serviceRequestId, ProviderOffer offer) {
        Optional<ServiceRequest> serviceRequest = serviceRequestRepository.findById(serviceRequestId);
        if (serviceRequest.isPresent()) {
            offer.setServiceRequest(serviceRequest.get());
            return providerOfferRepository.save(offer);
        }
        return null;
    }

    public ProviderOffer updateOfferStatus(Long offerId, String status) {
        Optional<ProviderOffer> optionalOffer = providerOfferRepository.findById(offerId);
        if (optionalOffer.isPresent()) {
            ProviderOffer offer = optionalOffer.get();
            // In a real application, you'd have proper enum conversion
            return providerOfferRepository.save(offer);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<ProviderOffer> getAllOffers() {
        return providerOfferRepository.findAll();
    }
}