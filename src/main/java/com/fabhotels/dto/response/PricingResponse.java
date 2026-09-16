package com.fabhotels.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PricingResponse {

    private Long id;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal pricePerNight;
    private Boolean active;
    private LocalDateTime createdAt;

    public PricingResponse() {
    }

    public PricingResponse(
            Long id,
            Long roomId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal pricePerNight,
            Boolean active,
            LocalDateTime createdAt) {

        this.id = id;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricePerNight = pricePerNight;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}