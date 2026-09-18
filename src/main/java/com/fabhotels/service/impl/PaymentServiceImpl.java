package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CreatePaymentRequest;
import com.fabhotels.dto.response.PaymentResponse;
import com.fabhotels.entity.Booking;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.entity.Payment;
import com.fabhotels.enums.PaymentStatus;
import com.fabhotels.exception.BookingNotFoundException;
import com.fabhotels.exception.BookingNotPayableException;
import com.fabhotels.exception.InvalidPaymentAmountException;
import com.fabhotels.exception.PaymentAlreadyExistsException;
import com.fabhotels.exception.PaymentNotFoundException;
import com.fabhotels.repository.BookingRepository;
import com.fabhotels.repository.PaymentRepository;
import com.fabhotels.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(
            CreatePaymentRequest request) {

        validateRequest(request);

        log.info(
                "Processing payment: bookingId={}, paymentMethod={}, amount={}",
                request.getBookingId(),
                request.getPaymentMethod(),
                request.getAmount()
        );


        Booking booking =
                bookingRepository.findById(
                        request.getBookingId()
                ).orElseThrow(() ->
                        new BookingNotFoundException(
                                request.getBookingId()
                        )
                );

        if (booking.getStatus() != BookingStatus.CONFIRMED) {

            throw new BookingNotPayableException(
                    "Only confirmed bookings can be paid"
            );
        }

        if (paymentRepository.existsByBookingId(
                booking.getId())) {

            throw new PaymentAlreadyExistsException(
                    booking.getId()
            );
        }

        if (request.getAmount()
                .compareTo(booking.getTotalAmount()) != 0) {

            throw new InvalidPaymentAmountException(
                    "Payment amount must match booking total amount"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Payment payment = new Payment();

        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(
                request.getPaymentMethod()
        );
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(
                generateTransactionId()
        );
        payment.setPaidAt(now);
        payment.setCreatedAt(now);

        Payment savedPayment =
                paymentRepository.save(payment);
        log.info(
                "Payment successful: paymentId={}, bookingId={}, amount={}, method={}",
                savedPayment.getId(),
                booking.getId(),
                savedPayment.getAmount(),
                savedPayment.getPaymentMethod()
        );


        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            Long paymentId) {

        Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        paymentId
                                )
                        );

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(
            Long bookingId) {

        if (!bookingRepository.existsById(bookingId)) {

            throw new BookingNotFoundException(
                    bookingId
            );
        }

        Payment payment =
                paymentRepository.findByBookingId(bookingId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        bookingId
                                )
                        );

        return mapToResponse(payment);
    }

    private void validateRequest(
            CreatePaymentRequest request) {

        if (request == null) {

            throw new InvalidPaymentAmountException(
                    "Payment request cannot be null"
            );
        }

        if (request.getBookingId() == null) {

            throw new InvalidPaymentAmountException(
                    "Booking id cannot be null"
            );
        }

        if (request.getAmount() == null) {

            throw new InvalidPaymentAmountException(
                    "Payment amount cannot be null"
            );
        }

        if (request.getAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidPaymentAmountException(
                    "Payment amount must be greater than 0"
            );
        }

        if (request.getPaymentMethod() == null) {

            throw new InvalidPaymentAmountException(
                    "Payment method cannot be null"
            );
        }
    }

    private String generateTransactionId() {

        return "TXN-"
                + LocalDateTime.now()
                .toString()
                .replaceAll("[^0-9]", "")
                .substring(0, 14)
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }

    private PaymentResponse mapToResponse(
            Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}