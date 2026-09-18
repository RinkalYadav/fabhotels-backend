package com.fabhotels.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request payload for creating room pricing")
public class CreatePricingRequest {

    @Schema(
            description = "Start date of the pricing period (inclusive)",
            example = "2026-09-20",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate startDate;

    @Schema(
            description = "End date of the pricing period (exclusive)",
            example = "2026-09-25",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate endDate;

    @Schema(
            description = "Price per night during this pricing period",
            example = "2999.00",
            minimum = "0.01",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal pricePerNight;

    public CreatePricingRequest() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
}