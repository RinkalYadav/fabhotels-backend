package com.fabhotels.dto.response;

import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;

import java.math.BigDecimal;

public class RoomResponse {

    private Long id;
    private Long hotelId;
    private String roomNumber;
    private RoomType roomType;
    private BigDecimal pricePerNight;
    private Integer capacity;
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