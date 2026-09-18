package com.fabhotels.dto.response;

import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Response containing room details")
public class RoomResponse {

    @Schema(
            description = "Unique room ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Unique hotel ID",
            example = "1"
    )
    private Long hotelId;

    @Schema(
            description = "Room number within the hotel",
            example = "101"
    )
    private String roomNumber;

    @Schema(
            description = "Type of the room",
            example = "DELUXE"
    )
    private RoomType roomType;

    @Schema(
            description = "Base price per night",
            example = "2499.00"
    )
    private BigDecimal pricePerNight;

    @Schema(
            description = "Maximum number of guests allowed",
            example = "2"
    )
    private Integer capacity;

    @Schema(
            description = "Current room status",
            example = "AVAILABLE"
    )
    private RoomStatus status;

    public RoomResponse() {
    }

    public RoomResponse(
            Long id,
            Long hotelId,
            String roomNumber,
            RoomType roomType,
            BigDecimal pricePerNight,
            Integer capacity,
            RoomStatus status
    ) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getHotelId() {
        return hotelId;
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

    public RoomStatus getStatus() {
        return status;
    }
}