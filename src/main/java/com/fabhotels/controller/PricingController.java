package com.fabhotels.controller;

import com.fabhotels.dto.request.CreatePricingRequest;
import com.fabhotels.dto.response.PricingResponse;
import com.fabhotels.service.PricingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(
            PricingService pricingService) {

        this.pricingService = pricingService;
    }

    @PostMapping("/{roomId}/pricing")
    public ResponseEntity<PricingResponse> createPricing(
            @PathVariable Long roomId,
            @RequestBody CreatePricingRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        pricingService.createPricing(
                                roomId,
                                request
                        )
                );
    }

    @GetMapping("/{roomId}/pricing")
    public ResponseEntity<List<PricingResponse>>
    getPricingByRoom(
            @PathVariable Long roomId) {

        return ResponseEntity.ok(
                pricingService.getPricingByRoom(roomId)
        );
    }

    @GetMapping("/{roomId}/pricing/check")
    public ResponseEntity<PricingResponse>
    getPricingForDate(
            @PathVariable Long roomId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                pricingService.getPricingForDate(
                        roomId,
                        date
                )
        );
    }
}