package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CreateRoomAvailabilityRequest;
import com.fabhotels.dto.response.RoomAvailabilityCheckResponse;
import com.fabhotels.dto.response.RoomAvailabilityResponse;
import com.fabhotels.entity.Room;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.exception.DuplicateAvailabilityException;
import com.fabhotels.exception.InvalidDateRangeException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.RoomAvailabilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RoomAvailabilityServiceImpl
        implements RoomAvailabilityService {

    private final RoomAvailabilityRepository
            availabilityRepository;

    private final RoomRepository roomRepository;

    public RoomAvailabilityServiceImpl(
            RoomAvailabilityRepository availabilityRepository,
            RoomRepository roomRepository
    ) {
        this.availabilityRepository =
                availabilityRepository;

        this.roomRepository = roomRepository;
    }

    @Override
    public RoomAvailabilityResponse createAvailability(
            Long roomId,
            CreateRoomAvailabilityRequest request
    ) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(
                        () -> new RoomNotFoundException(roomId)
                );

        boolean exists =
                availabilityRepository.existsByRoomIdAndDate(
                        roomId,
                        request.getDate()
                );

        if (exists) {
            throw new DuplicateAvailabilityException(
                    roomId,
                    request.getDate().toString()
            );
        }

        RoomAvailability availability =
                new RoomAvailability();

        availability.setRoom(room);
        availability.setDate(request.getDate());
        availability.setStatus(request.getStatus());

        RoomAvailability saved =
                availabilityRepository.save(availability);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAvailabilityResponse> getRoomAvailability(
            Long roomId
    ) {

        verifyRoomExists(roomId);

        return availabilityRepository
                .findByRoomIdOrderByDateAsc(roomId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAvailabilityResponse>
    getAvailabilityByDateRange(
            Long roomId,
            LocalDate from,
            LocalDate to
    ) {

        verifyRoomExists(roomId);

        validateDateRange(from, to);

        return availabilityRepository
                .findByRoomIdAndDateBetweenOrderByDateAsc(
                        roomId,
                        from,
                        to
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomAvailabilityCheckResponse checkAvailability(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(
                        () -> new RoomNotFoundException(roomId)
                );

        validateDateRange(checkIn, checkOut);

        /*
         * Maintenance and inactive rooms are never bookable,
         * regardless of date-specific availability.
         */
        if (room.getStatus() != RoomStatus.AVAILABLE) {

            return new RoomAvailabilityCheckResponse(
                    roomId,
                    checkIn,
                    checkOut,
                    false
            );
        }

        List<RoomAvailability> records =
                availabilityRepository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                roomId,
                                checkIn,
                                checkOut
                        );

        /*
         * If a date has an explicit BOOKED or BLOCKED record,
         * the room is unavailable.
         */
        boolean unavailable = records.stream()
                .anyMatch(record ->
                        record.getStatus() == AvailabilityStatus.BOOKED
                                || record.getStatus()
                                == AvailabilityStatus.BLOCKED
                );

        /*
         * For this implementation, every requested night must
         * have an AVAILABLE record.
         */
        long requestedNights =
                checkIn.until(
                        checkOut,
                        java.time.temporal.ChronoUnit.DAYS
                );

        long availableNights = records.stream()
                .filter(record ->
                        record.getStatus()
                                == AvailabilityStatus.AVAILABLE
                )
                .count();

        boolean available =
                !unavailable
                        && availableNights == requestedNights;

        return new RoomAvailabilityCheckResponse(
                roomId,
                checkIn,
                checkOut,
                available
        );
    }

    private void verifyRoomExists(Long roomId) {

        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
    }

    private void validateDateRange(
            LocalDate from,
            LocalDate to
    ) {

        if (from == null
                || to == null
                || !to.isAfter(from)) {

            throw new InvalidDateRangeException();
        }
    }

    private RoomAvailabilityResponse mapToResponse(
            RoomAvailability availability
    ) {

        return new RoomAvailabilityResponse(
                availability.getId(),
                availability.getRoom().getId(),
                availability.getDate(),
                availability.getStatus()
        );
    }
}