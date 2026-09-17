package com.fabhotels.controller;

import tools.jackson.databind.ObjectMapper;
import com.fabhotels.dto.request.CreatePaymentRequest;
import com.fabhotels.dto.response.PaymentResponse;
import com.fabhotels.enums.PaymentMethod;
import com.fabhotels.enums.PaymentStatus;
import com.fabhotels.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    private PaymentResponse response() {

        return new PaymentResponse(
                1L,
                10L,
                new BigDecimal("5000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-20260917-ABC123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createPayment_shouldReturn201()
            throws Exception {

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setBookingId(10L);
        request.setAmount(
                new BigDecimal("5000")
        );
        request.setPaymentMethod(
                PaymentMethod.UPI
        );

        when(
                paymentService.createPayment(
                        any(CreatePaymentRequest.class)
                )
        ).thenReturn(response());

        mockMvc.perform(
                        post("/api/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookingId").value(10))
                .andExpect(jsonPath("$.amount").value(5000))
                .andExpect(jsonPath("$.paymentMethod")
                        .value("UPI"))
                .andExpect(jsonPath("$.status")
                        .value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId")
                        .value("TXN-20260917-ABC123"));
    }

    @Test
    void getPaymentById_shouldReturn200()
            throws Exception {

        when(
                paymentService.getPaymentById(1L)
        ).thenReturn(response());

        mockMvc.perform(
                        get("/api/payments/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookingId").value(10))
                .andExpect(jsonPath("$.status")
                        .value("SUCCESS"));
    }
}