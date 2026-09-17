package com.fabhotels.controller;

import tools.jackson.databind.ObjectMapper;
import com.fabhotels.dto.request.CancelBookingRequest;
import com.fabhotels.dto.response.CancellationResponse;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.service.CancellationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CancellationController.class)
class CancellationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CancellationService cancellationService;

    @Test
    void cancelBooking_shouldReturn200()
            throws Exception {

        CancelBookingRequest request =
                new CancelBookingRequest();

        request.setReason("Change of plans");

        CancellationResponse response =
                new CancellationResponse(
                        101L,
                        BookingStatus.CANCELLED,
                        LocalDateTime.now(),
                        new BigDecimal("6000"),
                        "Change of plans"
                );

        when(
                cancellationService.cancelBooking(
                        eq(101L),
                        any(CancelBookingRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/bookings/101/cancel")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId")
                        .value(101))
                .andExpect(jsonPath("$.status")
                        .value("CANCELLED"))
                .andExpect(jsonPath("$.refundAmount")
                        .value(6000))
                .andExpect(jsonPath("$.reason")
                        .value("Change of plans"));
    }
}