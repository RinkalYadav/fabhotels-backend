package com.fabhotels.controller;

import com.fabhotels.dto.request.CreatePricingRequest;
import com.fabhotels.dto.response.PricingResponse;
import com.fabhotels.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(
        name = "Pricing",
        description = "Dynamic room pricing APIs"
)
@RestController
@RequestMapping("/api/rooms")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @Operation(
            summary = "Create room pricing",
            description = "Creates a dynamic pricing period for a room."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pricing created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pricing data or date range"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Pricing period overlaps with an existing active pricing period"
            )
    })
    @PostMapping("/{roomId}/pricing")
    public ResponseEntity<PricingResponse> createPricing(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pricing details including start date, end date, and price per night",
                    required = true
            )
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

    @Operation(
            summary = "Get pricing by room",
            description = "Returns all pricing periods configured for a room."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pricing records retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room not found"
            )
    })
    @GetMapping("/{roomId}/pricing")
    public ResponseEntity<List<PricingResponse>> getPricingByRoom(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId) {

        return ResponseEntity.ok(
                pricingService.getPricingByRoom(roomId)
        );
    }

    @Operation(
            summary = "Get pricing for a date",
            description = "Returns the active pricing applicable to a room for the specified date."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pricing retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pricing not found for the specified room and date"
            )
    })
    @GetMapping("/{roomId}/pricing/check")
    public ResponseEntity<PricingResponse> getPricingForDate(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId,

            @Parameter(
                    description = "Date for which pricing should be checked",
                    example = "2026-09-20",
                    required = true
            )
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                pricingService.getPricingForDate(
                        roomId,
                        date
                )
        );
    }
}