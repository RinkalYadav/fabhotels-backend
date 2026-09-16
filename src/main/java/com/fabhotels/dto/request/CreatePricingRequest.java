package com.fabhotels.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePricingRequest {

    private LocalDate startDate;
    private LocalDate endDate;
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