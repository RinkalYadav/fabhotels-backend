package com.fabhotels.service;

import com.fabhotels.dto.request.CreateRoomAvailabilityRequest;
import com.fabhotels.dto.response.RoomAvailabilityCheckResponse;
import com.fabhotels.dto.response.RoomAvailabilityResponse;
import com.fabhotels.entity.Room;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import com.fabhotels.exception.DuplicateAvailabilityException;
import com.fabhotels.exception.InvalidDateRangeException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.impl.RoomAvailabilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomAvailabilityServiceImplTest {

    @Mock
    private RoomAvailabilityRepository availabilityRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomAvailabilityServiceImpl availabilityService;

    private Room room;

    @BeforeEach
    void setUp() {

        room = new Room();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPricePerNight(
                new BigDecimal("3500.00")
        );
        room.setCapacity(2);
        room.setStatus(RoomStatus.AVAILABLE);
    }

    // =========================================================
    // CREATE AVAILABILITY
    // =========================================================

    @Test
    void createAvailability_whenRoomExists_shouldCreate() {

        LocalDate date = LocalDate.of(2026, 9, 18);

        CreateRoomAvailabilityRequest request =
                new CreateRoomAvailabilityRequest();

        request.setDate(date);
        request.setStatus(AvailabilityStatus.AVAILABLE);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                availabilityRepository.existsByRoomIdAndDate(
                        1L,
                        date
                )
        ).thenReturn(false);

        RoomAvailability saved =
                new RoomAvailability();

        saved.setId(100L);
        saved.setRoom(room);
        saved.setDate(date);
        saved.setStatus(AvailabilityStatus.AVAILABLE);

        when(availabilityRepository.save(any(RoomAvailability.class)))
                .thenReturn(saved);

        RoomAvailabilityResponse response =
                availabilityService.createAvailability(
                        1L,
                        request
                );

        assertNotNull(response);

        assertEquals(100L, response.getId());
        assertEquals(1L, response.getRoomId());
        assertEquals(date, response.getDate());

        assertEquals(
                AvailabilityStatus.AVAILABLE,
                response.getStatus()
        );

        verify(roomRepository).findById(1L);

        verify(
                availabilityRepository
        ).existsByRoomIdAndDate(1L, date);

        verify(
                availabilityRepository
        ).save(any(RoomAvailability.class));
    }

    @Test
    void createAvailability_whenRoomDoesNotExist_shouldThrowException() {

        LocalDate date = LocalDate.of(2026, 9, 18);

        CreateRoomAvailabilityRequest request =
                new CreateRoomAvailabilityRequest();

        request.setDate(date);
        request.setStatus(AvailabilityStatus.AVAILABLE);

        when(roomRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RoomNotFoundException.class,
                () -> availabilityService.createAvailability(
                        999L,
                        request
                )
        );

        verify(
                availabilityRepository,
                never()
        ).save(any(RoomAvailability.class));
    }

    @Test
    void createAvailability_whenDuplicateDate_shouldThrowException() {

        LocalDate date = LocalDate.of(2026, 9, 18);

        CreateRoomAvailabilityRequest request =
                new CreateRoomAvailabilityRequest();

        request.setDate(date);
        request.setStatus(AvailabilityStatus.AVAILABLE);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                availabilityRepository.existsByRoomIdAndDate(
                        1L,
                        date
                )
        ).thenReturn(true);

        assertThrows(
                DuplicateAvailabilityException.class,
                () -> availabilityService.createAvailability(
                        1L,
                        request
                )
        );

        verify(
                availabilityRepository,
                never()
        ).save(any(RoomAvailability.class));
    }

    // =========================================================
    // GET ALL AVAILABILITY
    // =========================================================

    @Test
    void getRoomAvailability_whenRoomExists_shouldReturnRecords() {

        LocalDate date1 =
                LocalDate.of(2026, 9, 18);

        LocalDate date2 =
                LocalDate.of(2026, 9, 19);

        RoomAvailability availability1 =
                createAvailability(
                        1L,
                        date1,
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability availability2 =
                createAvailability(
                        2L,
                        date2,
                        AvailabilityStatus.BOOKED
                );

        when(roomRepository.existsById(1L))
                .thenReturn(true);

        when(
                availabilityRepository
                        .findByRoomIdOrderByDateAsc(1L)
        ).thenReturn(
                List.of(
                        availability1,
                        availability2
                )
        );

        List<RoomAvailabilityResponse> response =
                availabilityService.getRoomAvailability(1L);

        assertNotNull(response);

        assertEquals(2, response.size());

        assertEquals(
                date1,
                response.get(0).getDate()
        );

        assertEquals(
                date2,
                response.get(1).getDate()
        );

        assertEquals(
                AvailabilityStatus.AVAILABLE,
                response.get(0).getStatus()
        );

        assertEquals(
                AvailabilityStatus.BOOKED,
                response.get(1).getStatus()
        );
    }

    @Test
    void getRoomAvailability_whenRoomDoesNotExist_shouldThrowException() {

        when(roomRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                RoomNotFoundException.class,
                () ->
                        availabilityService
                                .getRoomAvailability(999L)
        );

        verify(
                availabilityRepository,
                never()
        ).findByRoomIdOrderByDateAsc(999L);
    }

    // =========================================================
    // DATE RANGE
    // =========================================================

    @Test
    void getAvailabilityByDateRange_shouldReturnRecords() {

        LocalDate from =
                LocalDate.of(2026, 9, 18);

        LocalDate to =
                LocalDate.of(2026, 9, 20);

        RoomAvailability availability =
                createAvailability(
                        1L,
                        LocalDate.of(2026, 9, 18),
                        AvailabilityStatus.AVAILABLE
                );

        when(roomRepository.existsById(1L))
                .thenReturn(true);

        when(
                availabilityRepository
                        .findByRoomIdAndDateBetweenOrderByDateAsc(
                                1L,
                                from,
                                to
                        )
        ).thenReturn(List.of(availability));

        List<RoomAvailabilityResponse> response =
                availabilityService.getAvailabilityByDateRange(
                        1L,
                        from,
                        to
                );

        assertNotNull(response);

        assertEquals(1, response.size());

        assertEquals(
                LocalDate.of(2026, 9, 18),
                response.get(0).getDate()
        );
    }

    @Test
    void getAvailabilityByDateRange_whenInvalidRange_shouldThrowException() {

        LocalDate from =
                LocalDate.of(2026, 9, 20);

        LocalDate to =
                LocalDate.of(2026, 9, 18);

        when(roomRepository.existsById(1L))
                .thenReturn(true);

        assertThrows(
                InvalidDateRangeException.class,
                () ->
                        availabilityService
                                .getAvailabilityByDateRange(
                                        1L,
                                        from,
                                        to
                                )
        );
    }

    // =========================================================
    // CHECK AVAILABILITY
    // =========================================================

    @Test
    void checkAvailability_whenAllDatesAvailable_shouldReturnTrue() {

        LocalDate checkIn =
                LocalDate.of(2026, 9, 18);

        LocalDate checkOut =
                LocalDate.of(2026, 9, 20);

        RoomAvailability day1 =
                createAvailability(
                        1L,
                        LocalDate.of(2026, 9, 18),
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability day2 =
                createAvailability(
                        2L,
                        LocalDate.of(2026, 9, 19),
                        AvailabilityStatus.AVAILABLE
                );

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                availabilityRepository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                1L,
                                checkIn,
                                checkOut
                        )
        ).thenReturn(
                List.of(day1, day2)
        );

        RoomAvailabilityCheckResponse response =
                availabilityService.checkAvailability(
                        1L,
                        checkIn,
                        checkOut
                );

        assertTrue(response.isAvailable());

        assertEquals(1L, response.getRoomId());

        assertEquals(
                checkIn,
                response.getCheckIn()
        );

        assertEquals(
                checkOut,
                response.getCheckOut()
        );
    }

    @Test
    void checkAvailability_whenDateBooked_shouldReturnFalse() {

        LocalDate checkIn =
                LocalDate.of(2026, 9, 18);

        LocalDate checkOut =
                LocalDate.of(2026, 9, 20);

        RoomAvailability day1 =
                createAvailability(
                        1L,
                        LocalDate.of(2026, 9, 18),
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability day2 =
                createAvailability(
                        2L,
                        LocalDate.of(2026, 9, 19),
                        AvailabilityStatus.BOOKED
                );

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                availabilityRepository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                1L,
                                checkIn,
                                checkOut
                        )
        ).thenReturn(
                List.of(day1, day2)
        );

        RoomAvailabilityCheckResponse response =
                availabilityService.checkAvailability(
                        1L,
                        checkIn,
                        checkOut
                );

        assertFalse(response.isAvailable());
    }

    @Test
    void checkAvailability_whenDateBlocked_shouldReturnFalse() {

        LocalDate checkIn =
                LocalDate.of(2026, 9, 18);

        LocalDate checkOut =
                LocalDate.of(2026, 9, 20);

        RoomAvailability day1 =
                createAvailability(
                        1L,
                        LocalDate.of(2026, 9, 18),
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability day2 =
                createAvailability(
                        2L,
                        LocalDate.of(2026, 9, 19),
                        AvailabilityStatus.BLOCKED
                );

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                availabilityRepository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                1L,
                                checkIn,
                                checkOut
                        )
        ).thenReturn(
                List.of(day1, day2)
        );

        RoomAvailabilityCheckResponse response =
                availabilityService.checkAvailability(
                        1L,
                        checkIn,
                        checkOut
                );

        assertFalse(response.isAvailable());
    }

    @Test
    void checkAvailability_whenRoomMaintenance_shouldReturnFalse() {

        room.setStatus(RoomStatus.MAINTENANCE);

        LocalDate checkIn =
                LocalDate.of(2026, 9, 18);

        LocalDate checkOut =
                LocalDate.of(2026, 9, 20);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        RoomAvailabilityCheckResponse response =
                availabilityService.checkAvailability(
                        1L,
                        checkIn,
                        checkOut
                );

        assertFalse(response.isAvailable());

        verify(
                availabilityRepository,
                never()
        )
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        anyLong(),
                        any(LocalDate.class),
                        any(LocalDate.class)
                );
    }

    @Test
    void checkAvailability_whenRoomInactive_shouldReturnFalse() {

        room.setStatus(RoomStatus.INACTIVE);

        LocalDate checkIn =
                LocalDate.of(2026, 9, 18);

        LocalDate checkOut =
                LocalDate.of(2026, 9, 20);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        RoomAvailabilityCheckResponse response =
                availabilityService.checkAvailability(
                        1L,
                        checkIn,
                        checkOut
                );

        assertFalse(response.isAvailable());
    }

    @Test
    void checkAvailability_whenInvalidDateRange_shouldThrowException() {

        LocalDate checkIn =
                LocalDate.of(2026, 9, 20);

        LocalDate checkOut =
                LocalDate.of(2026, 9, 18);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        assertThrows(
                InvalidDateRangeException.class,
                () ->
                        availabilityService
                                .checkAvailability(
                                        1L,
                                        checkIn,
                                        checkOut
                                )
        );
    }

    @Test
    void checkAvailability_whenCheckInEqualsCheckOut_shouldThrowException() {

        LocalDate date =
                LocalDate.of(2026, 9, 18);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        assertThrows(
                InvalidDateRangeException.class,
                () ->
                        availabilityService
                                .checkAvailability(
                                        1L,
                                        date,
                                        date
                                )
        );
    }

    @Test
    void checkAvailability_whenOneDateMissing_shouldReturnFalse() {

        LocalDate checkIn =
                LocalDate.of(2026, 9, 18);

        LocalDate checkOut =
                LocalDate.of(2026, 9, 21);

        RoomAvailability day1 =
                createAvailability(
                        1L,
                        LocalDate.of(2026, 9, 18),
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability day2 =
                createAvailability(
                        2L,
                        LocalDate.of(2026, 9, 19),
                        AvailabilityStatus.AVAILABLE
                );

        // 20 Sep has no availability record

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                availabilityRepository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                1L,
                                checkIn,
                                checkOut
                        )
        ).thenReturn(
                List.of(day1, day2)
        );

        RoomAvailabilityCheckResponse response =
                availabilityService.checkAvailability(
                        1L,
                        checkIn,
                        checkOut
                );

        assertFalse(response.isAvailable());
    }

    // =========================================================
    // HELPER METHOD
    // =========================================================

    private RoomAvailability createAvailability(
            Long id,
            LocalDate date,
            AvailabilityStatus status
    ) {

        RoomAvailability availability =
                new RoomAvailability();

        availability.setId(id);
        availability.setRoom(room);
        availability.setDate(date);
        availability.setStatus(status);

        return availability;
    }
}