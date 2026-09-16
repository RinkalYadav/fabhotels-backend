package com.fabhotels.service;

import com.fabhotels.dto.request.CreatePricingRequest;
import com.fabhotels.dto.response.PricingResponse;

import java.time.LocalDate;
import java.util.List;

public interface PricingService {

    PricingResponse createPricing(
            Long roomId,
            CreatePricingRequest request
    );

    List<PricingResponse> getPricingByRoom(Long roomId);

    PricingResponse getPricingForDate(
            Long roomId,
            LocalDate date
    );
}