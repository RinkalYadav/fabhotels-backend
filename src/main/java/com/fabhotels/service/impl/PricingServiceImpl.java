package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CreatePricingRequest;
import com.fabhotels.dto.response.PricingResponse;
import com.fabhotels.entity.Pricing;
import com.fabhotels.entity.Room;
import com.fabhotels.exception.DuplicatePricingException;
import com.fabhotels.exception.InvalidPricingAmountException;
import com.fabhotels.exception.InvalidPricingDateException;
import com.fabhotels.exception.PricingNotFoundException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.PricingRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.PricingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PricingServiceImpl implements PricingService {

    private final PricingRepository pricingRepository;
    private final RoomRepository roomRepository;

    public PricingServiceImpl(
            PricingRepository pricingRepository,
            RoomRepository roomRepository) {

        this.pricingRepository = pricingRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    @PreAuthorize("hasRole('HOTEL_ADMIN')")
    @Transactional
    public PricingResponse createPricing(
            Long roomId,
            CreatePricingRequest request) {

        validateRequest(request);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RoomNotFoundException(roomId));

        boolean overlapping =
                pricingRepository
                        .existsByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThan(
                                roomId,
                                request.getEndDate(),
                                request.getStartDate()
                        );

        if (overlapping) {

            throw new DuplicatePricingException(
                    "Active pricing already exists for the requested date range"
            );
        }

        Pricing pricing = new Pricing();

        pricing.setRoom(room);
        pricing.setStartDate(request.getStartDate());
        pricing.setEndDate(request.getEndDate());
        pricing.setPricePerNight(request.getPricePerNight());
        pricing.setActive(true);

        Pricing savedPricing =
                pricingRepository.save(pricing);

        return mapToResponse(savedPricing);
    }

    @Override
    @PreAuthorize("hasRole('HOTEL_ADMIN')")
    @Transactional(readOnly = true)
    public List<PricingResponse> getPricingByRoom(
            Long roomId) {

        if (!roomRepository.existsById(roomId)) {

            throw new RoomNotFoundException(roomId);
        }

        return pricingRepository
                .findByRoomIdOrderByStartDateAsc(roomId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PricingResponse getPricingForDate(
            Long roomId,
            LocalDate date) {

        if (!roomRepository.existsById(roomId)) {

            throw new RoomNotFoundException(roomId);
        }

        if (date == null) {

            throw new InvalidPricingDateException(
                    "Date cannot be null"
            );
        }

        Pricing pricing =
                pricingRepository
                        .findFirstByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanAndActiveTrue(
                                roomId,
                                date,
                                date
                        )
                        .orElseThrow(() ->
                                new PricingNotFoundException(
                                        roomId
                                ));

        return mapToResponse(pricing);
    }

    private void validateRequest(
            CreatePricingRequest request) {

        if (request == null) {

            throw new InvalidPricingDateException(
                    "Pricing request cannot be null"
            );
        }

        if (request.getStartDate() == null) {

            throw new InvalidPricingDateException(
                    "Start date cannot be null"
            );
        }

        if (request.getEndDate() == null) {

            throw new InvalidPricingDateException(
                    "End date cannot be null"
            );
        }

        if (!request.getEndDate()
                .isAfter(request.getStartDate())) {

            throw new InvalidPricingDateException(
                    "End date must be after start date"
            );
        }

        if (request.getPricePerNight() == null ||
                request.getPricePerNight()
                        .compareTo(java.math.BigDecimal.ZERO) <= 0) {

            throw new InvalidPricingAmountException(
                    "Price per night must be greater than 0"
            );
        }
    }

    private PricingResponse mapToResponse(
            Pricing pricing) {

        return new PricingResponse(
                pricing.getId(),
                pricing.getRoom().getId(),
                pricing.getStartDate(),
                pricing.getEndDate(),
                pricing.getPricePerNight(),
                pricing.getActive(),
                pricing.getCreatedAt()
        );
    }
}