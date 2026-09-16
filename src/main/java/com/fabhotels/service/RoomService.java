package com.fabhotels.service;

import com.fabhotels.dto.request.CreateRoomRequest;
import com.fabhotels.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {

    RoomResponse createRoom(
            Long hotelId,
            CreateRoomRequest request
    );

    RoomResponse getRoomById(Long roomId);

    List<RoomResponse> getRoomsByHotelId(Long hotelId);
}