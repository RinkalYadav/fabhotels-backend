package com.fabhotels.controller;

import com.fabhotels.dto.request.CreateHotelRequest;
import com.fabhotels.dto.request.HotelSearchRequest;
import com.fabhotels.dto.response.HotelResponse;
import com.fabhotels.dto.response.HotelSearchPageResponse;
import com.fabhotels.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Hotels",
        description = "Hotel management APIs"
)
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Operation(
            summary = "Create a hotel",
            description = "Creates a new hotel using the provided hotel details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Hotel created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid hotel data"
            )
    })
    @PostMapping
    public ResponseEntity<HotelResponse> createHotel(
            @Valid @RequestBody CreateHotelRequest request) {

        HotelResponse response = hotelService.createHotel(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get hotel by ID",
            description = "Returns the details of a hotel using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Hotel retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hotel not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> getHotelById(
            @Parameter(
                    description = "Unique hotel ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        HotelResponse response = hotelService.getHotelById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all hotels",
            description = "Returns a list of all hotels."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Hotels retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<HotelResponse>> getAllHotels() {

        List<HotelResponse> hotels = hotelService.getAllHotels();

        return ResponseEntity.ok(hotels);
    }

    @Operation(
            summary = "Search hotels",
            description = "Searches hotels using optional city, state, and active status filters with pagination and sorting."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Hotel search completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<HotelSearchPageResponse> searchHotels(

            @Parameter(
                    description = "Filter hotels by city",
                    example = "Bangalore"
            )
            @RequestParam(required = false) String city,

            @Parameter(
                    description = "Filter hotels by state",
                    example = "Karnataka"
            )
            @RequestParam(required = false) String state,

            @Parameter(
                    description = "Filter hotels by active status",
                    example = "true"
            )
            @RequestParam(required = false) Boolean active,

            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "Number of hotels per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size,

            @Parameter(
                    description = "Field used for sorting",
                    example = "name"
            )
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(
                    description = "Sort direction: asc or desc",
                    example = "asc"
            )
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        HotelSearchRequest request = new HotelSearchRequest();

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