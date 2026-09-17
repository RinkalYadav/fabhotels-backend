package com.fabhotels.controller;

import com.fabhotels.dto.response.PaymentResponse;
import com.fabhotels.enums.PaymentMethod;
import com.fabhotels.enums.PaymentStatus;
import com.fabhotels.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingPaymentController.class)
class BookingPaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void getPaymentByBookingId_shouldReturn200()
            throws Exception {

        PaymentResponse payment =
                new PaymentResponse(
                        1L,
                        10L,
                        new BigDecimal("5000"),
                        PaymentMethod.UPI,
                        PaymentStatus.SUCCESS,
                        "TXN-1",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(
                paymentService.getPaymentByBookingId(10L)
        ).thenReturn(payment);

        mockMvc.perform(
                        get("/api/bookings/10/payment")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookingId").value(10))
                .andExpect(jsonPath("$.amount").value(5000))
                .andExpect(jsonPath("$.paymentMethod")
                        .value("UPI"))
                .andExpect(jsonPath("$.status")
                        .value("SUCCESS"));
    }
}