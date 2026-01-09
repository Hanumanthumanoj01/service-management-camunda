package com.frauas.servicemanagement.controller;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.entity.ServiceRequest;
import com.frauas.servicemanagement.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/reporting")
@CrossOrigin(origins = "*") // Critical for Power BI access
public class ReportingController {

    @Autowired
    private ReportingService reportingService;

    // --- 1. EXISTING SUMMARIES (Keep for high-level KPIs) ---
    @GetMapping("/requests/summary")
    public Map<String, Object> getRequestSummary() {
        return reportingService.getServiceRequestSummary();
    }

    @GetMapping("/offers/statistics")
    public Map<String, Object> getOfferStats() {
        return reportingService.getOfferStatistics();
    }

    @GetMapping("/providers/rankings")
    public List<Map<String, Object>> getRankings() {
        return reportingService.getProviderRankings();
    }

    // --- 2. NEW: RAW DATASETS (What Group 5b requested) ---

    @GetMapping("/requests/all")
    public List<ServiceRequest> getAllRequests() {
        List<ServiceRequest> data = reportingService.getAllRequestsRaw();
        System.out.println("\n========== [API REPORTING] POWER BI FETCHING REQUESTS ==========");
        System.out.println("Action: Sending full dataset (" + data.size() + " records)");
        System.out.println("==============================================================\n");
        return data;
    }

    @GetMapping("/offers/all")
    public List<ProviderOffer> getAllOffers() {
        List<ProviderOffer> data = reportingService.getAllOffersRaw();
        System.out.println("\n========== [API REPORTING] POWER BI FETCHING OFFERS ==========");
        System.out.println("Action: Sending full dataset (" + data.size() + " records)");
        System.out.println("============================================================\n");
        return data;
    }
}