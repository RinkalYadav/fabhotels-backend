package com.fabhotels.dto.response;

import com.fabhotels.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Response containing booking details")
public class BookingResponse {

    @Schema(description = "Unique booking ID", example = "101")
    private Long id;

    @Schema(description = "ID of the booked room", example = "10")
    private Long roomId;

    @Schema(description = "Name of the guest", example = "Rinkal Yadav")
    private String guestName;

    @Schema(description = "Email address of the guest", example = "rinkal@example.com")
    private String guestEmail;

    @Schema(description = "Check-in date", example = "2026-10-10")
    private LocalDate checkIn;

    @Schema(description = "Check-out date", example = "2026-10-12")
    private LocalDate checkOut;

    @Schema(description = "Number of guests", example = "2")
    private Integer numberOfGuests;

    @Schema(description = "Total booking amount", example = "5000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Current booking status", example = "CONFIRMED")
    private BookingStatus status;

    @Schema(description = "Date and time when the booking was created", example = "2026-09-18T10:30:00")
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(
            Long id,
            Long roomId,
            String guestName,
            String guestEmail,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer numberOfGuests,
            BigDecimal totalAmount,
            BookingStatus status,
            LocalDateTime createdAt) {

        this.id = id;
        this.roomId = roomId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}