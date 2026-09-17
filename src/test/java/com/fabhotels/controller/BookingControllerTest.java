package com.fabhotels.controller;

import tools.jackson.databind.ObjectMapper;
import com.fabhotels.dto.request.CreateBookingRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    private CreateBookingRequest request() {

        CreateBookingRequest request =
                new CreateBookingRequest();

        request.setRoomId(10L);
        request.setGuestName("Rinkal");
        request.setGuestEmail("rinkal@test.com");
        request.setCheckIn(
                LocalDate.of(2030, 1, 1)
        );
        request.setCheckOut(
                LocalDate.of(2030, 1, 3)
        );
        request.setNumberOfGuests(2);

        return request;
    }

    private BookingResponse response() {

        return new BookingResponse(
                20L,
                10L,
                "Rinkal",
                "rinkal@test.com",
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2030, 1, 3),
                2,
                new BigDecimal("5000"),
                BookingStatus.CONFIRMED,
                LocalDateTime.now()
        );
    }

    @Test
    void createBooking_shouldReturn201() throws Exception {

        when(
                bookingService.createBooking(
                        any(CreateBookingRequest.class)
                )
        ).thenReturn(response());

        mockMvc.perform(
                        post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request()
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.roomId").value(10))
                .andExpect(jsonPath("$.guestName")
                        .value("Rinkal"))
                .andExpect(jsonPath("$.status")
                        .value("CONFIRMED"));
    }

    @Test
    void getBookingById_shouldReturn200() throws Exception {

        when(
                bookingService.getBookingById(20L)
        ).thenReturn(response());

        mockMvc.perform(
                        get("/api/bookings/20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.roomId").value(10))
                .andExpect(jsonPath("$.status")
                        .value("CONFIRMED"));
    }
}