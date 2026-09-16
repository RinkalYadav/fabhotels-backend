package com.fabhotels.service;

import com.fabhotels.dto.request.CreatePaymentRequest;
import com.fabhotels.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(
            CreatePaymentRequest request
    );

    PaymentResponse getPaymentById(
            Long paymentId
    );

    PaymentResponse getPaymentByBookingId(
            Long bookingId
    );
}