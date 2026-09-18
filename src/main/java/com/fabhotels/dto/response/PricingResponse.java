package com.fabhotels.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Response containing room pricing details")
public class PricingResponse {

    @Schema(description = "Unique pricing ID", example = "201")
    private Long id;

    @Schema(description = "ID of the room", example = "10")
    private Long roomId;

    @Schema(description = "Pricing start date (inclusive)", example = "2026-10-01")
    private LocalDate startDate;

    @Schema(description = "Pricing end date (exclusive)", example = "2026-10-10")
    private LocalDate endDate;

    @Schema(description = "Price per night", example = "2500.00")
    private BigDecimal pricePerNight;

    @Schema(description = "Whether this pricing rule is active", example = "true")
    private Boolean active;

    @Schema(description = "Date and time when the pricing record was created", example = "2026-09-18T10:00:00")
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