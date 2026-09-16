package com.fabhotels.service;

import com.fabhotels.dto.request.CreateHotelRequest;
import com.fabhotels.dto.request.HotelSearchRequest;
import com.fabhotels.dto.response.HotelResponse;
import com.fabhotels.dto.response.HotelSearchPageResponse;

import java.util.List;

public interface HotelService {
    HotelResponse createHotel(CreateHotelRequest request);
    HotelResponse getHotelById(Long id);
    List<HotelResponse> getAllHotels();

    HotelSearchPageResponse searchHotels(
            HotelSearchRequest request
    );
}
