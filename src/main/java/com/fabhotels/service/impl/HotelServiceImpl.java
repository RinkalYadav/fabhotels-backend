package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CreateHotelRequest;
import com.fabhotels.dto.response.HotelResponse;
import com.fabhotels.entity.Hotel;
import com.fabhotels.exception.HotelNotFoundException;
import com.fabhotels.repository.HotelRepository;
import com.fabhotels.service.HotelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository){
        this.hotelRepository=hotelRepository;
    }

    public HotelResponse createHotel(CreateHotelRequest request){
        Hotel hotel=new Hotel();

        hotel.setName(request.getName());
        hotel.setDescription(request.getDescription());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setState(request.getState());
        hotel.setCountry(request.getCountry());
        hotel.setPincode(request.getPincode());

        Hotel savedHotel = hotelRepository.save(hotel);

        return mapToResponse(savedHotel);
    }

    @Override
    public HotelResponse getHotelById(Long id) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        return mapToResponse(hotel);
    }
    @Override
    public List<HotelResponse> getAllHotels() {

        return hotelRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private HotelResponse mapToResponse(Hotel hotel) {

        HotelResponse response = new HotelResponse();

        response.setId(hotel.getId());
        response.setName(hotel.getName());
        response.setDescription(hotel.getDescription());
        response.setAddress(hotel.getAddress());
        response.setCity(hotel.getCity());
        response.setState(hotel.getState());
        response.setCountry(hotel.getCountry());
        response.setPincode(hotel.getPincode());
        response.setActive(hotel.isActive());

        return response;
    }
}
