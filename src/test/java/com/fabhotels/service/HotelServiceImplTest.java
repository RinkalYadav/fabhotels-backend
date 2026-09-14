package com.fabhotels.service;

import com.fabhotels.dto.CreateHotelRequest;
import com.fabhotels.dto.HotelResponse;
import com.fabhotels.entity.Hotel;
import com.fabhotels.exception.HotelNotFoundException;
import com.fabhotels.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private CreateHotelRequest request;
    private Hotel hotel;

    @BeforeEach
    void setUp() {

        request = new CreateHotelRequest();

        request.setName("Fab Grand Hotel");
        request.setDescription("Comfortable hotel near the city center");
        request.setAddress("123 MG Road");
        request.setCity("Bengaluru");
        request.setState("Karnataka");
        request.setCountry("India");
        request.setPincode("560001");

        hotel = new Hotel();

        hotel.setId(1L);
        hotel.setName("Fab Grand Hotel");
        hotel.setDescription("Comfortable hotel near the city center");
        hotel.setAddress("123 MG Road");
        hotel.setCity("Bengaluru");
        hotel.setState("Karnataka");
        hotel.setCountry("India");
        hotel.setPincode("560001");
        hotel.setActive(true);
    }

    @Test
    void createHotel_shouldReturnHotelResponse() {

        when(hotelRepository.save(any(Hotel.class)))
                .thenReturn(hotel);

        HotelResponse response = hotelService.createHotel(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Fab Grand Hotel", response.getName());
        assertEquals("Bengaluru", response.getCity());
        assertEquals("560001", response.getPincode());
        assertTrue(response.isActive());

        verify(hotelRepository, times(1))
                .save(any(Hotel.class));
    }

    @Test
    void getHotelById_shouldReturnHotelResponse() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));

        HotelResponse response = hotelService.getHotelById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Fab Grand Hotel", response.getName());

        verify(hotelRepository, times(1))
                .findById(1L);
    }

    @Test
    void getHotelById_whenHotelDoesNotExist_shouldThrowException() {

        when(hotelRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                HotelNotFoundException.class,
                () -> hotelService.getHotelById(999L)
        );

        verify(hotelRepository, times(1))
                .findById(999L);
    }

    @Test
    void getAllHotels_shouldReturnHotelList() {

        Hotel secondHotel = new Hotel();

        secondHotel.setId(2L);
        secondHotel.setName("Fab Comfort Hotel");
        secondHotel.setCity("Delhi");
        secondHotel.setState("Delhi");
        secondHotel.setCountry("India");
        secondHotel.setAddress("456 Main Road");
        secondHotel.setPincode("110001");
        secondHotel.setActive(true);

        when(hotelRepository.findAll())
                .thenReturn(List.of(hotel, secondHotel));

        List<HotelResponse> responses =
                hotelService.getAllHotels();

        assertEquals(2, responses.size());
        assertEquals("Fab Grand Hotel", responses.get(0).getName());
        assertEquals("Fab Comfort Hotel", responses.get(1).getName());

        verify(hotelRepository, times(1))
                .findAll();
    }
}