package com.fabhotels.service;

import com.fabhotels.dto.request.CreatePricingRequest;
import com.fabhotels.dto.response.PricingResponse;
import com.fabhotels.entity.Pricing;
import com.fabhotels.entity.Room;
import com.fabhotels.exception.DuplicatePricingException;
import com.fabhotels.exception.InvalidPricingAmountException;
import com.fabhotels.exception.InvalidPricingDateException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.PricingRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.impl.PricingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock
    private PricingRepository pricingRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private PricingServiceImpl pricingService;

    private Room room;
    private CreatePricingRequest request;

    @BeforeEach
    void setUp() {

        room = new Room();

        room.setId(1L);
        room.setPricePerNight(
                new BigDecimal("2000.00")
        );

        request = new CreatePricingRequest();

        request.setStartDate(
                LocalDate.of(2026, 10, 10)
        );

        request.setEndDate(
                LocalDate.of(2026, 10, 20)
        );

        request.setPricePerNight(
                new BigDecimal("3000.00")
        );
    }

    @Test
    void createPricing_shouldCreateSuccessfully() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                pricingRepository
                        .existsByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThan(
                                eq(1L),
                                eq(LocalDate.of(2026, 10, 20)),
                                eq(LocalDate.of(2026, 10, 10))
                        )
        ).thenReturn(false);

        when(pricingRepository.save(any(Pricing.class)))
                .thenAnswer(invocation -> {

                    Pricing pricing =
                            invocation.getArgument(0);

                    pricing.setId(1L);
                    pricing.setCreatedAt(
                            LocalDateTime.of(
                                    2026,
                                    9,
                                    16,
                                    12,
                                    30
                            )
                    );

                    return pricing;
                });

        PricingResponse response =
                pricingService.createPricing(
                        1L,
                        request
                );

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getRoomId());

        assertEquals(
                new BigDecimal("3000.00"),
                response.getPricePerNight()
        );

        assertTrue(response.getActive());

        verify(pricingRepository, times(1))
                .save(any(Pricing.class));
    }

    @Test
    void createPricing_whenRoomDoesNotExist_shouldThrowException() {

        when(roomRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RoomNotFoundException.class,
                () -> pricingService.createPricing(
                        999L,
                        request
                )
        );

        verifyNoInteractions(pricingRepository);
    }

    @Test
    void createPricing_whenStartDateIsNull_shouldThrowException() {

        request.setStartDate(null);

        assertThrows(
                InvalidPricingDateException.class,
                () -> pricingService.createPricing(
                        1L,
                        request
                )
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createPricing_whenEndDateIsNull_shouldThrowException() {

        request.setEndDate(null);

        assertThrows(
                InvalidPricingDateException.class,
                () -> pricingService.createPricing(
                        1L,
                        request
                )
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createPricing_whenEndDateIsBeforeStartDate_shouldThrowException() {

        request.setEndDate(
                LocalDate.of(2026, 10, 9)
        );

        assertThrows(
                InvalidPricingDateException.class,
                () -> pricingService.createPricing(
                        1L,
                        request
                )
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createPricing_whenDatesAreEqual_shouldThrowException() {

        request.setEndDate(
                LocalDate.of(2026, 10, 10)
        );

        assertThrows(
                InvalidPricingDateException.class,
                () -> pricingService.createPricing(
                        1L,
                        request
                )
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createPricing_whenPriceIsZero_shouldThrowException() {

        request.setPricePerNight(
                BigDecimal.ZERO
        );

        assertThrows(
                InvalidPricingAmountException.class,
                () -> pricingService.createPricing(
                        1L,
                        request
                )
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createPricing_whenPriceIsNegative_shouldThrowException() {

        request.setPricePerNight(
                new BigDecimal("-100")
        );

        assertThrows(
                InvalidPricingAmountException.class,
                () -> pricingService.createPricing(
                        1L,
                        request
                )
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createPricing_whenPricingOverlaps_shouldThrowException() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                pricingRepository
                        .existsByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThan(
                                eq(1L),
                                eq(LocalDate.of(2026, 10, 20)),
                                eq(LocalDate.of(2026, 10, 10))
                        )
        ).thenReturn(true);

        assertThrows(
                DuplicatePricingException.class,
                () -> pricingService.createPricing(
                        1L,
                        request
                )
        );

        verify(pricingRepository, never())
                .save(any(Pricing.class));
    }

    @Test
    void createPricing_adjacentPeriod_shouldBeAllowed() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(
                pricingRepository
                        .existsByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThan(
                                eq(1L),
                                eq(LocalDate.of(2026, 10, 20)),
                                eq(LocalDate.of(2026, 10, 10))
                        )
        ).thenReturn(false);

        when(pricingRepository.save(any(Pricing.class)))
                .thenAnswer(invocation -> {

                    Pricing pricing =
                            invocation.getArgument(0);

                    pricing.setId(2L);

                    return pricing;
                });

        PricingResponse response =
                pricingService.createPricing(
                        1L,
                        request
                );

        assertNotNull(response);
        assertEquals(2L, response.getId());
    }

    @Test
    void getPricingByRoom_shouldReturnPricingList() {

        Pricing pricing = createPricing();

        when(roomRepository.existsById(1L))
                .thenReturn(true);

        when(pricingRepository
                .findByRoomIdOrderByStartDateAsc(1L))
                .thenReturn(List.of(pricing));

        List<PricingResponse> responses =
                pricingService.getPricingByRoom(1L);

        assertEquals(1, responses.size());
        assertEquals(
                new BigDecimal("3000.00"),
                responses.get(0).getPricePerNight()
        );
    }

    @Test
    void getPricingByRoom_whenRoomDoesNotExist_shouldThrowException() {

        when(roomRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                RoomNotFoundException.class,
                () -> pricingService.getPricingByRoom(999L)
        );

        verify(pricingRepository, never())
                .findByRoomIdOrderByStartDateAsc(anyLong());
    }

    @Test
    void getPricingForDate_shouldReturnApplicablePricing() {

        Pricing pricing = createPricing();

        when(roomRepository.existsById(1L))
                .thenReturn(true);

        when(
                pricingRepository
                        .findFirstByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanAndActiveTrue(
                                eq(1L),
                                eq(LocalDate.of(2026, 10, 15)),
                                eq(LocalDate.of(2026, 10, 15))
                        )
        ).thenReturn(Optional.of(pricing));

        PricingResponse response =
                pricingService.getPricingForDate(
                        1L,
                        LocalDate.of(2026, 10, 15)
                );

        assertNotNull(response);

        assertEquals(
                new BigDecimal("3000.00"),
                response.getPricePerNight()
        );
    }

    private Pricing createPricing() {

        Pricing pricing = new Pricing();

        pricing.setId(1L);
        pricing.setRoom(room);
        pricing.setStartDate(
                LocalDate.of(2026, 10, 10)
        );
        pricing.setEndDate(
                LocalDate.of(2026, 10, 20)
        );
        pricing.setPricePerNight(
                new BigDecimal("3000.00")
        );
        pricing.setActive(true);
        pricing.setCreatedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        12,
                        30
                )
        );

        return pricing;
    }
}