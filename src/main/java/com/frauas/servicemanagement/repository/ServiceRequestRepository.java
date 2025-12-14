package com.frauas.servicemanagement.repository;

import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.entity.ServiceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByStatus(ServiceRequestStatus status);
    List<ServiceRequest> findByOrderByCreatedAtDesc();
    List<ServiceRequest> findAllByOrderByCreatedAtDesc();
}