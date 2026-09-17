package com.fabhotels.service;

import com.fabhotels.dto.request.CancelBookingRequest;
import com.fabhotels.dto.response.CancellationResponse;

public interface CancellationService {

    CancellationResponse cancelBooking(
            Long bookingId,
            CancelBookingRequest request
    );
}