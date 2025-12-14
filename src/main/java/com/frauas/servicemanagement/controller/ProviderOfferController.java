package com.frauas.servicemanagement.controller;

import com.frauas.servicemanagement.entity.ProviderOffer;
import com.frauas.servicemanagement.service.ProviderOfferService;
import com.frauas.servicemanagement.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/offers")
public class ProviderOfferController {

    @Autowired
    private ProviderOfferService providerOfferService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping
    public String getAllOffers(Model model) {
        List<ProviderOffer> offers = providerOfferService.getAllOffers();
        model.addAttribute("offers", offers);
        return "offers";
    }

    @GetMapping("/request/{requestId}")
    public String getOffersForRequest(@PathVariable Long requestId, Model model) {
        List<ProviderOffer> offers = providerOfferService.getOffersByServiceRequest(requestId);
        model.addAttribute("offers", offers);
        model.addAttribute("serviceRequestId", requestId);
        model.addAttribute("newOffer", new ProviderOffer());
        return "request-offers";
    }

    @PostMapping("/request/{requestId}")
    public String submitOffer(@PathVariable Long requestId, @ModelAttribute ProviderOffer offer) {
        providerOfferService.submitOffer(requestId, offer);
        return "redirect:/offers/request/" + requestId;
    }

    // REST API endpoints for provider management group
    @GetMapping("/api/request/{requestId}")
    @ResponseBody
    public List<ProviderOffer> getOffersForRequestApi(@PathVariable Long requestId) {
        return providerOfferService.getOffersByServiceRequest(requestId);
    }

    @PostMapping("/api/request/{requestId}")
    @ResponseBody
    public ProviderOffer submitOfferApi(@PathVariable Long requestId, @RequestBody ProviderOffer offer) {
        return providerOfferService.submitOffer(requestId, offer);
    }
}