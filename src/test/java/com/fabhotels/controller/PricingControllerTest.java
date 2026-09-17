package com.fabhotels.controller;

import tools.jackson.databind.ObjectMapper;
import com.fabhotels.dto.request.CreatePricingRequest;
import com.fabhotels.dto.response.PricingResponse;
import com.fabhotels.service.PricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PricingController.class)
class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PricingService pricingService;

    private PricingResponse response() {

        return new PricingResponse(
                1L,
                10L,
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2030, 2, 1),
                new BigDecimal("3000"),
                true,
                LocalDateTime.of(
                        2030,
                        1,
                        1,
                        10,
                        0
                )
        );
    }

    @Test
    void createPricing_shouldReturn201()
            throws Exception {

        CreatePricingRequest request =
                new CreatePricingRequest();

        request.setStartDate(
                LocalDate.of(2030, 1, 1)
        );

        request.setEndDate(
                LocalDate.of(2030, 2, 1)
        );

        request.setPricePerNight(
                new BigDecimal("3000")
        );

        when(
                pricingService.createPricing(
                        eq(10L),
                        any(CreatePricingRequest.class)
                )
        ).thenReturn(response());

        mockMvc.perform(
                        post("/api/rooms/10/pricing")
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
                .andExpect(jsonPath("$.startDate")
                        .value("2030-01-01"))
                .andExpect(jsonPath("$.endDate")
                        .value("2030-02-01"))
                .andExpect(jsonPath("$.pricePerNight")
                        .value(3000))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }

    @Test
    void getPricingByRoom_shouldReturn200()
            throws Exception {

        when(
                pricingService.getPricingByRoom(10L)
        ).thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/rooms/10/pricing")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].roomId").value(10))
                .andExpect(jsonPath("$[0].pricePerNight")
                        .value(3000));
    }

    @Test
    void getPricingForDate_shouldReturn200()
            throws Exception {

        when(
                pricingService.getPricingForDate(
                        10L,
                        LocalDate.of(2030, 1, 10)
                )
        ).thenReturn(response());

        mockMvc.perform(
                        get("/api/rooms/10/pricing/check")
                                .param(
                                        "date",
                                        "2030-01-10"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roomId").value(10))
                .andExpect(jsonPath("$.pricePerNight")
                        .value(3000))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }
}