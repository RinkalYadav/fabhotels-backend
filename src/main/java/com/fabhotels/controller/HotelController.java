package com.fabhotels.controller;

import com.fabhotels.dto.CreateHotelRequest;
import com.fabhotels.dto.HotelResponse;
import com.fabhotels.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}