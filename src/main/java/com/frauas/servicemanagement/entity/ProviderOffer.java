package com.frauas.servicemanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "provider_offers")
public class ProviderOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;

    private String providerName;
    private Double proposedRate;
    private String expertProfile;
    private Integer deliveryTimeDays;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    private LocalDateTime submittedAt;

    // Constructors
    public ProviderOffer() {
        this.submittedAt = LocalDateTime.now();
        this.status = OfferStatus.SUBMITTED;
    }

    public ProviderOffer(ServiceRequest serviceRequest, String providerName,
                         Double proposedRate, String expertProfile, Integer deliveryTimeDays) {
        this();
        this.serviceRequest = serviceRequest;
        this.providerName = providerName;
        this.proposedRate = proposedRate;
        this.expertProfile = expertProfile;
        this.deliveryTimeDays = deliveryTimeDays;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ServiceRequest getServiceRequest() { return serviceRequest; }
    public void setServiceRequest(ServiceRequest serviceRequest) { this.serviceRequest = serviceRequest; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public Double getProposedRate() { return proposedRate; }
    public void setProposedRate(Double proposedRate) { this.proposedRate = proposedRate; }

    public String getExpertProfile() { return expertProfile; }
    public void setExpertProfile(String expertProfile) { this.expertProfile = expertProfile; }

    public Integer getDeliveryTimeDays() { return deliveryTimeDays; }
    public void setDeliveryTimeDays(Integer deliveryTimeDays) { this.deliveryTimeDays = deliveryTimeDays; }

    public OfferStatus getStatus() { return status; }
    public void setStatus(OfferStatus status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}