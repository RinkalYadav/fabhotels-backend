package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CreateHotelRequest;
import com.fabhotels.dto.request.HotelSearchRequest;
import com.fabhotels.dto.response.HotelResponse;
import com.fabhotels.dto.response.HotelSearchPageResponse;
import com.fabhotels.dto.response.HotelSearchResponse;
import com.fabhotels.entity.Hotel;
import com.fabhotels.exception.HotelNotFoundException;
import com.fabhotels.exception.InvalidSearchParameterException;
import com.fabhotels.repository.HotelRepository;
import com.fabhotels.service.HotelService;
import com.fabhotels.specification.HotelSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    // ============================
    // CREATE HOTEL
    // ============================

    @Override
    public HotelResponse createHotel(CreateHotelRequest request) {

        Hotel hotel = new Hotel();

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

    // ============================
    // GET HOTEL BY ID
    // ============================

    @Override
    public HotelResponse getHotelById(Long id) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        return mapToResponse(hotel);
    }

    // ============================
    // GET ALL HOTELS
    // ============================

    @Override
    public List<HotelResponse> getAllHotels() {

        return hotelRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================
    // FAB-105 HOTEL SEARCH
    // ============================

    @Override
    public HotelSearchPageResponse searchHotels(
            HotelSearchRequest request) {

        // Validate request first
        validateSearchRequest(request);

        // ============================
        // SORTING
        // ============================

        Sort.Direction direction =
                request.getSortDirection()
                        .equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Sort sort = Sort.by(
                direction,
                request.getSortBy().trim()
        );

        // ============================
        // PAGINATION
        // ============================

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );

        // ============================
        // SPECIFICATION
        // ============================

        Specification<Hotel> specification =
                (root, query, criteriaBuilder) -> null;

        // Filter by city
        if (request.getCity() != null
                && !request.getCity().trim().isEmpty()) {

            specification = specification.and(
                    HotelSpecification.hasCity(
                            request.getCity().trim()
                    )
            );
        }

        // Filter by state
        if (request.getState() != null
                && !request.getState().trim().isEmpty()) {

            specification = specification.and(
                    HotelSpecification.hasState(
                            request.getState().trim()
                    )
            );
        }

        // Filter by active status
        if (request.getActive() != null) {

            specification = specification.and(
                    HotelSpecification.hasActive(
                            request.getActive()
                    )
            );
        }

        // ============================
        // DATABASE QUERY
        // ============================

        Page<Hotel> hotelPage =
                hotelRepository.findAll(
                        specification,
                        pageable
                );

        // ============================
        // RESPONSE MAPPING
        // ============================

        List<HotelSearchResponse> content =
                hotelPage.getContent()
                        .stream()
                        .map(this::mapToSearchResponse)
                        .toList();

        return new HotelSearchPageResponse(
                content,
                hotelPage.getNumber(),
                hotelPage.getSize(),
                hotelPage.getTotalElements(),
                hotelPage.getTotalPages()
        );
    }

    // ============================
    // SEARCH VALIDATION
    // ============================

    private void validateSearchRequest(
            HotelSearchRequest request) {

        // Null request
        if (request == null) {
            throw new InvalidSearchParameterException(
                    "Search request cannot be null"
            );
        }

        // Page validation
        if (request.getPage() < 0) {
            throw new InvalidSearchParameterException(
                    "Page must be greater than or equal to 0"
            );
        }

        // Size validation
        if (request.getSize() <= 0) {
            throw new InvalidSearchParameterException(
                    "Size must be greater than 0"
            );
        }

        if (request.getSize() > 100) {
            throw new InvalidSearchParameterException(
                    "Size cannot be greater than 100"
            );
        }

        // Sort field validation
        if (request.getSortBy() == null
                || request.getSortBy().trim().isEmpty()) {

            throw new InvalidSearchParameterException(
                    "Sort field cannot be null or empty"
            );
        }

        Set<String> allowedSortFields =
                Set.of(
                        "id",
                        "name",
                        "city",
                        "state"
                );

        String sortBy =
                request.getSortBy()
                        .trim()
                        .toLowerCase();

        if (!allowedSortFields.contains(sortBy)) {

            throw new InvalidSearchParameterException(
                    "Invalid sort field: "
                            + request.getSortBy()
            );
        }

        // Sort direction validation
        if (request.getSortDirection() == null
                || request.getSortDirection()
                .trim()
                .isEmpty()) {

            throw new InvalidSearchParameterException(
                    "Sort direction cannot be null or empty"
            );
        }

        if (!request.getSortDirection()
                .equalsIgnoreCase("asc")
                && !request.getSortDirection()
                .equalsIgnoreCase("desc")) {

            throw new InvalidSearchParameterException(
                    "Sort direction must be either asc or desc"
            );
        }
    }

    // ============================
    // SEARCH RESPONSE MAPPING
    // ============================

    private HotelSearchResponse mapToSearchResponse(
            Hotel hotel) {

        return new HotelSearchResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getState(),
                hotel.getCountry(),
                hotel.isActive()
        );
    }

    // ============================
    // HOTEL RESPONSE MAPPING
    // ============================

    private HotelResponse mapToResponse(
            Hotel hotel) {

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