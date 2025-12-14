package com.frauas.servicemanagement.repository;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderOfferRepository extends JpaRepository<ProviderOffer, Long> {
    List<ProviderOffer> findByServiceRequest(ServiceRequest serviceRequest);
    List<ProviderOffer> findByServiceRequestId(Long serviceRequestId);
    // NEW METHOD FOR CASCADING DELETE
    void deleteByServiceRequest(ServiceRequest serviceRequest);

}