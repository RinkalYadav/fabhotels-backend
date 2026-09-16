package com.fabhotels.service;

import com.fabhotels.dto.request.CreateRoomAvailabilityRequest;
import com.fabhotels.dto.response.RoomAvailabilityCheckResponse;
import com.fabhotels.dto.response.RoomAvailabilityResponse;

import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityService {

    RoomAvailabilityResponse createAvailability(
            Long roomId,
            CreateRoomAvailabilityRequest request
    );

    List<RoomAvailabilityResponse> getRoomAvailability(
            Long roomId
    );

    List<RoomAvailabilityResponse> getAvailabilityByDateRange(
            Long roomId,
            LocalDate from,
            LocalDate to
    );

    RoomAvailabilityCheckResponse checkAvailability(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    );
}