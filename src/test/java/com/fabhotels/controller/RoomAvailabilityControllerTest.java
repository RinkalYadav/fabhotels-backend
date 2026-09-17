package com.fabhotels.controller;

import tools.jackson.databind.ObjectMapper;
import com.fabhotels.dto.request.CreateRoomAvailabilityRequest;
import com.fabhotels.dto.response.RoomAvailabilityCheckResponse;
import com.fabhotels.dto.response.RoomAvailabilityResponse;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.service.RoomAvailabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomAvailabilityController.class)
class RoomAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomAvailabilityService service;

    private RoomAvailabilityResponse response() {

        return new RoomAvailabilityResponse(
                1L,
                10L,
                LocalDate.of(2030, 1, 1),
                AvailabilityStatus.AVAILABLE
        );
    }

    @Test
    void createAvailability_shouldReturn201()
            throws Exception {

        CreateRoomAvailabilityRequest request =
                new CreateRoomAvailabilityRequest();

        request.setDate(
                LocalDate.of(2030, 1, 1)
        );

        request.setStatus(
                AvailabilityStatus.AVAILABLE
        );

        when(
                service.createAvailability(
                        any(Long.class),
                        any(CreateRoomAvailabilityRequest.class)
                )
        ).thenReturn(response());

        mockMvc.perform(
                        post("/api/rooms/10/availability")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roomId").value(10))
                .andExpect(jsonPath("$.date")
                        .value("2030-01-01"))
                .andExpect(jsonPath("$.status")
                        .value("AVAILABLE"));
    }

    @Test
    void createAvailability_withMissingDate_shouldReturn400()
            throws Exception {

        CreateRoomAvailabilityRequest request =
                new CreateRoomAvailabilityRequest();

        request.setStatus(
                AvailabilityStatus.AVAILABLE
        );

        mockMvc.perform(
                        post("/api/rooms/10/availability")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAvailability_shouldReturn200()
            throws Exception {

        when(
                service.getRoomAvailability(10L)
        ).thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/rooms/10/availability")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1))
                .andExpect(jsonPath("$[0].date")
                        .value("2030-01-01"));
    }

    @Test
    void getAvailabilityRange_shouldReturn200()
            throws Exception {

        when(
                service.getAvailabilityByDateRange(
                        10L,
                        LocalDate.of(2030, 1, 1),
                        LocalDate.of(2030, 1, 5)
                )
        ).thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/rooms/10/availability/range")
                                .param(
                                        "from",
                                        "2030-01-01"
                                )
                                .param(
                                        "to",
                                        "2030-01-05"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1));
    }

    @Test
    void checkAvailability_shouldReturn200()
            throws Exception {

        when(
                service.checkAvailability(
                        10L,
                        LocalDate.of(2030, 1, 1),
                        LocalDate.of(2030, 1, 3)
                )
        ).thenReturn(
                new RoomAvailabilityCheckResponse(
                        10L,
                        LocalDate.of(2030, 1, 1),
                        LocalDate.of(2030, 1, 3),
                        true
                )
        );

        mockMvc.perform(
                        get("/api/rooms/10/availability/check")
                                .param(
                                        "checkIn",
                                        "2030-01-01"
                                )
                                .param(
                                        "checkOut",
                                        "2030-01-03"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId")
                        .value(10))
                .andExpect(jsonPath("$.available")
                        .value(true));
    }
}