package com.fabhotels.controller;

import com.fabhotels.dto.request.CreateHotelRequest;
import com.fabhotels.dto.request.HotelSearchRequest;
import com.fabhotels.dto.response.HotelResponse;
import com.fabhotels.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.fabhotels.dto.request.HotelSearchRequest;
import com.fabhotels.dto.response.HotelSearchPageResponse;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    public ResponseEntity<HotelResponse> createHotel(
            @Valid @RequestBody CreateHotelRequest request) {

        HotelResponse response = hotelService.createHotel(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> getHotelById(
            @PathVariable Long id) {

        HotelResponse response = hotelService.getHotelById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<HotelResponse>> getAllHotels() {

        List<HotelResponse> hotels = hotelService.getAllHotels();

        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/search")
    public ResponseEntity<HotelSearchPageResponse>
    searchHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        HotelSearchRequest request =
                new HotelSearchRequest();

        request.setCity(city);
        request.setState(state);
        request.setActive(active);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);

        return ResponseEntity.ok(
                hotelService.searchHotels(request)
        );
    }
}