package com.frauas.servicemanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private ServiceRequestStatus status;

    private String requiredSkills;
    private Integer durationDays;
    private String projectContext;

    // Mock data from other groups
    private Long contractId;
    private Long internalRequestId;

    // NEW: Store why a request was rejected
    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructors ---
    public ServiceRequest() {
        this.createdAt = LocalDateTime.now();
        this.status = ServiceRequestStatus.DRAFT;
    }

    public ServiceRequest(String title, String description, String requiredSkills,
                          Integer durationDays, String projectContext) {
        this();
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.durationDays = durationDays;
        this.projectContext = projectContext;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ServiceRequestStatus getStatus() { return status; }
    public void setStatus(ServiceRequestStatus status) { this.status = status; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public String getProjectContext() { return projectContext; }
    public void setProjectContext(String projectContext) { this.projectContext = projectContext; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public Long getInternalRequestId() { return internalRequestId; }
    public void setInternalRequestId(Long internalRequestId) { this.internalRequestId = internalRequestId; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- JPA lifecycle hooks ---
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        // ensure a non-null status on persist
        if (this.status == null) {
            this.status = ServiceRequestStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
