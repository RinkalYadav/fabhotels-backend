package com.fabhotels.dto.request;

import com.fabhotels.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Request payload for creating a room")
public class CreateRoomRequest {

    @NotBlank(message = "Room number is required")
    @Schema(
            description = "Room number within the hotel",
            example = "101",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String roomNumber;

    @NotNull(message = "Room type is required")
    @Schema(
            description = "Type of the room",
            example = "DELUXE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private RoomType roomType;

    @NotNull(message = "Price per night is required")
    @DecimalMin(
            value = "0.01",
            message = "Price per night must be greater than 0"
    )
    @Schema(
            description = "Base price per night",
            example = "2499.00",
            minimum = "0.01",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal pricePerNight;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than 0")
    @Schema(
            description = "Maximum number of guests allowed in the room",
            example = "2",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer capacity;

    public CreateRoomRequest() {
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}