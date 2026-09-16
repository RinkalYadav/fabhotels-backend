package com.fabhotels.service;

import com.fabhotels.dto.request.CreatePaymentRequest;
import com.fabhotels.dto.response.PaymentResponse;
import com.fabhotels.entity.Booking;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.entity.Payment;
import com.fabhotels.enums.PaymentMethod;
import com.fabhotels.enums.PaymentStatus;
import com.fabhotels.exception.BookingNotPayableException;
import com.fabhotels.exception.BookingNotFoundException;
import com.fabhotels.exception.InvalidPaymentAmountException;
import com.fabhotels.exception.PaymentAlreadyExistsException;
import com.fabhotels.exception.PaymentNotFoundException;
import com.fabhotels.repository.BookingRepository;
import com.fabhotels.repository.PaymentRepository;
import com.fabhotels.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Booking booking;
    private CreatePaymentRequest request;

    @BeforeEach
    void setUp() {

        booking = new Booking();

        booking.setId(101L);
        booking.setTotalAmount(
                new BigDecimal("6000.00")
        );
        booking.setStatus(
                BookingStatus.CONFIRMED
        );

        request = new CreatePaymentRequest();

        request.setBookingId(101L);
        request.setAmount(
                new BigDecimal("6000.00")
        );
        request.setPaymentMethod(
                PaymentMethod.UPI
        );
    }

    // =========================================================
    // SUCCESSFUL PAYMENT
    // =========================================================

    @Test
    void createPayment_shouldCreateSuccessfulPayment() {

        when(bookingRepository.findById(101L))
                .thenReturn(Optional.of(booking));

        when(paymentRepository.existsByBookingId(101L))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {

                    Payment payment =
                            invocation.getArgument(0);

                    payment.setId(501L);

                    return payment;
                });

        PaymentResponse response =
                paymentService.createPayment(request);

        assertNotNull(response);

        assertEquals(
                501L,
                response.getId()
        );

        assertEquals(
                101L,
                response.getBookingId()
        );

        assertEquals(
                new BigDecimal("6000.00"),
                response.getAmount()
        );

        assertEquals(
                PaymentMethod.UPI,
                response.getPaymentMethod()
        );

        assertEquals(
                PaymentStatus.SUCCESS,
                response.getStatus()
        );

        assertNotNull(
                response.getTransactionId()
        );

        assertTrue(
                response.getTransactionId()
                        .startsWith("TXN-")
        );

        assertNotNull(
                response.getPaidAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        verify(paymentRepository, times(1))
                .save(any(Payment.class));
    }

    // =========================================================
    // BOOKING VALIDATION
    // =========================================================

    @Test
    void createPayment_whenBookingDoesNotExist_shouldThrowException() {

        when(bookingRepository.findById(101L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void createPayment_whenBookingIsCancelled_shouldThrowException() {

        booking.setStatus(
                BookingStatus.CANCELLED
        );

        when(bookingRepository.findById(101L))
                .thenReturn(Optional.of(booking));

        assertThrows(
                BookingNotPayableException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    // =========================================================
    // AMOUNT VALIDATION
    // =========================================================

    @Test
    void createPayment_whenAmountDoesNotMatchBooking_shouldThrowException() {

        request.setAmount(
                new BigDecimal("5000.00")
        );

        when(bookingRepository.findById(101L))
                .thenReturn(Optional.of(booking));

        when(paymentRepository.existsByBookingId(101L))
                .thenReturn(false);

        assertThrows(
                InvalidPaymentAmountException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void createPayment_whenAmountIsZero_shouldThrowException() {

        request.setAmount(
                BigDecimal.ZERO
        );

        assertThrows(
                InvalidPaymentAmountException.class,
                () -> paymentService.createPayment(request)
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void createPayment_whenAmountIsNegative_shouldThrowException() {

        request.setAmount(
                new BigDecimal("-100")
        );

        assertThrows(
                InvalidPaymentAmountException.class,
                () -> paymentService.createPayment(request)
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void createPayment_whenAmountIsNull_shouldThrowException() {

        request.setAmount(null);

        assertThrows(
                InvalidPaymentAmountException.class,
                () -> paymentService.createPayment(request)
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(paymentRepository);
    }

    // =========================================================
    // DUPLICATE PAYMENT
    // =========================================================

    @Test
    void createPayment_whenPaymentAlreadyExists_shouldThrowException() {

        when(bookingRepository.findById(101L))
                .thenReturn(Optional.of(booking));

        when(paymentRepository.existsByBookingId(101L))
                .thenReturn(true);

        assertThrows(
                PaymentAlreadyExistsException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    // =========================================================
    // PAYMENT METHOD
    // =========================================================

    @Test
    void createPayment_whenPaymentMethodIsNull_shouldThrowException() {

        request.setPaymentMethod(null);

        assertThrows(
                InvalidPaymentAmountException.class,
                () -> paymentService.createPayment(request)
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(paymentRepository);
    }

    // =========================================================
    // GET PAYMENT
    // =========================================================

    @Test
    void getPaymentById_shouldReturnPayment() {

        Payment payment = createPayment();

        when(paymentRepository.findById(501L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response =
                paymentService.getPaymentById(501L);

        assertNotNull(response);

        assertEquals(
                501L,
                response.getId()
        );

        assertEquals(
                101L,
                response.getBookingId()
        );

        assertEquals(
                new BigDecimal("6000.00"),
                response.getAmount()
        );

        assertEquals(
                PaymentStatus.SUCCESS,
                response.getStatus()
        );

        verify(paymentRepository, times(1))
                .findById(501L);
    }

    @Test
    void getPaymentById_whenPaymentDoesNotExist_shouldThrowException() {

        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(999L)
        );

        verify(paymentRepository, times(1))
                .findById(999L);
    }

    // =========================================================
    // GET PAYMENT BY BOOKING
    // =========================================================

    @Test
    void getPaymentByBookingId_shouldReturnPayment() {

        Payment payment = createPayment();

        when(bookingRepository.existsById(101L))
                .thenReturn(true);

        when(paymentRepository.findByBookingId(101L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response =
                paymentService.getPaymentByBookingId(101L);

        assertNotNull(response);

        assertEquals(
                501L,
                response.getId()
        );

        assertEquals(
                101L,
                response.getBookingId()
        );

        verify(paymentRepository, times(1))
                .findByBookingId(101L);
    }

    @Test
    void getPaymentByBookingId_whenBookingDoesNotExist_shouldThrowException() {

        when(bookingRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                BookingNotFoundException.class,
                () -> paymentService.getPaymentByBookingId(999L)
        );

        verify(paymentRepository, never())
                .findByBookingId(anyLong());
    }

    @Test
    void getPaymentByBookingId_whenPaymentDoesNotExist_shouldThrowException() {

        when(bookingRepository.existsById(101L))
                .thenReturn(true);

        when(paymentRepository.findByBookingId(101L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentByBookingId(101L)
        );
    }

    // =========================================================
    // HELPER
    // =========================================================

    private Payment createPayment() {

        Payment payment = new Payment();

        payment.setId(501L);
        payment.setBooking(booking);
        payment.setAmount(
                new BigDecimal("6000.00")
        );
        payment.setPaymentMethod(
                PaymentMethod.UPI
        );
        payment.setStatus(
                PaymentStatus.SUCCESS
        );
        payment.setTransactionId(
                "TXN-20260916-ABC123"
        );
        payment.setPaidAt(
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        14,
                        30
                )
        );
        payment.setCreatedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        14,
                        30
                )
        );

        return payment;
    }
}