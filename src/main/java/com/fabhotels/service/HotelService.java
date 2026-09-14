package com.fabhotels.service;

import com.fabhotels.dto.CreateHotelRequest;
import com.fabhotels.dto.HotelResponse;

import java.util.List;

public interface HotelService {
    HotelResponse createHotel(CreateHotelRequest request);
    HotelResponse getHotelById(Long id);
    List<HotelResponse> getAllHotels();
}
