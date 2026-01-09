package com.frauas.servicemanagement.entity;

public enum ServiceRequestStatus {
    DRAFT,
    WAITING_APPROVAL,
    NEEDS_CORRECTION, // For Ping-Pong loop
    PUBLISHED,
    OFFERS_RECEIVED,
    UNDER_EVALUATION,
    SELECTED_UNDER_VERIFICATION, // Critical new status
    VERIFIED,
    COMPLETED,
    REJECTED
}