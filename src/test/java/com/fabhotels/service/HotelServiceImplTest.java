package com.fabhotels.service;

import com.fabhotels.dto.request.CreateHotelRequest;
import com.fabhotels.dto.request.HotelSearchRequest;
import com.fabhotels.dto.response.HotelResponse;
import com.fabhotels.dto.response.HotelSearchPageResponse;
import com.fabhotels.entity.Hotel;
import com.fabhotels.exception.HotelNotFoundException;
import com.fabhotels.exception.InvalidSearchParameterException;
import com.fabhotels.repository.HotelRepository;
import com.fabhotels.service.impl.HotelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    // =========================================================
    // CREATE HOTEL TESTS
    // =========================================================

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

    // =========================================================
    // GET HOTEL BY ID TESTS
    // =========================================================

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

    // =========================================================
    // GET ALL HOTELS TESTS
    // =========================================================

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

    // =========================================================
    // FAB-105 HOTEL SEARCH TESTS
    // =========================================================

    @Test
    void searchHotels_byCity_shouldReturnMatchingHotels() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setCity("Bengaluru");
        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        Page<Hotel> page = new PageImpl<>(
                List.of(hotel)
        );

        when(hotelRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        HotelSearchPageResponse response =
                hotelService.searchHotels(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(
                "Fab Grand Hotel",
                response.getContent().get(0).getName()
        );
        assertEquals("Bengaluru",
                response.getContent().get(0).getCity());

        verify(hotelRepository, times(1))
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    void searchHotels_withoutFilters_shouldReturnHotels() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        Hotel secondHotel = new Hotel();

        secondHotel.setId(2L);
        secondHotel.setName("Fab Comfort Hotel");
        secondHotel.setCity("Delhi");
        secondHotel.setState("Delhi");
        secondHotel.setCountry("India");
        secondHotel.setActive(true);

        Page<Hotel> page = new PageImpl<>(
                List.of(hotel, secondHotel)
        );

        when(hotelRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        HotelSearchPageResponse response =
                hotelService.searchHotels(searchRequest);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());

        verify(hotelRepository, times(1))
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    void searchHotels_withPagination_shouldReturnPaginationMetadata() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setPage(0);
        searchRequest.setSize(1);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        Page<Hotel> page = new PageImpl<>(
                List.of(hotel),
                org.springframework.data.domain.PageRequest.of(0, 1),
                5
        );

        when(hotelRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        HotelSearchPageResponse response =
                hotelService.searchHotels(searchRequest);

        assertEquals(0, response.getPage());
        assertEquals(1, response.getSize());
        assertEquals(5, response.getTotalElements());
        assertEquals(5, response.getTotalPages());
        assertEquals(1, response.getContent().size());
    }

    @Test
    void searchHotels_byState_shouldReturnMatchingHotels() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setState("Karnataka");
        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        Page<Hotel> page = new PageImpl<>(
                List.of(hotel)
        );

        when(hotelRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        HotelSearchPageResponse response =
                hotelService.searchHotels(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(
                "Karnataka",
                response.getContent().get(0).getState()
        );
    }

    @Test
    void searchHotels_byActiveStatus_shouldReturnMatchingHotels() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setActive(true);
        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        Page<Hotel> page = new PageImpl<>(
                List.of(hotel)
        );

        when(hotelRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        HotelSearchPageResponse response =
                hotelService.searchHotels(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertTrue(
                response.getContent().get(0).getActive()
        );
    }

    @Test
    void searchHotels_withNoResults_shouldReturnEmptyContent() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setCity("Mumbai");
        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        Page<Hotel> emptyPage = new PageImpl<>(
                List.of(),
                org.springframework.data.domain.PageRequest.of(0, 10),
                0
        );

        when(hotelRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(emptyPage);

        HotelSearchPageResponse response =
                hotelService.searchHotels(searchRequest);

        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
    }

    // =========================================================
    // SEARCH VALIDATION TESTS
    // =========================================================

    @Test
    void searchHotels_withNegativePage_shouldThrowException() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setPage(-1);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        assertThrows(
                InvalidSearchParameterException.class,
                () -> hotelService.searchHotels(searchRequest)
        );

        verifyNoInteractions(hotelRepository);
    }

    @Test
    void searchHotels_withZeroSize_shouldThrowException() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setPage(0);
        searchRequest.setSize(0);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        assertThrows(
                InvalidSearchParameterException.class,
                () -> hotelService.searchHotels(searchRequest)
        );

        verifyNoInteractions(hotelRepository);
    }

    @Test
    void searchHotels_withSizeGreaterThan100_shouldThrowException() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setPage(0);
        searchRequest.setSize(101);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("asc");

        assertThrows(
                InvalidSearchParameterException.class,
                () -> hotelService.searchHotels(searchRequest)
        );

        verifyNoInteractions(hotelRepository);
    }

    @Test
    void searchHotels_withInvalidSortField_shouldThrowException() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("description");
        searchRequest.setSortDirection("asc");

        assertThrows(
                InvalidSearchParameterException.class,
                () -> hotelService.searchHotels(searchRequest)
        );

        verifyNoInteractions(hotelRepository);
    }

    @Test
    void searchHotels_withInvalidSortDirection_shouldThrowException() {

        HotelSearchRequest searchRequest = new HotelSearchRequest();

        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("id");
        searchRequest.setSortDirection("random");

        assertThrows(
                InvalidSearchParameterException.class,
                () -> hotelService.searchHotels(searchRequest)
        );

        verifyNoInteractions(hotelRepository);
    }
}