package com.fabhotels.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Request payload for creating a hotel room booking")
public class CreateBookingRequest {

    @Schema(
            description = "Unique room ID to book",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long roomId;

    @Schema(
            description = "Name of the guest",
            example = "Rinkal Yadav",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String guestName;

    @Schema(
            description = "Email address of the guest",
            example = "guest@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String guestEmail;

    @Schema(
            description = "Check-in date",
            example = "2026-09-20",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate checkIn;

    @Schema(
            description = "Check-out date",
            example = "2026-09-25",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate checkOut;

    @Schema(
            description = "Number of guests staying in the room",
            example = "2",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer numberOfGuests;

    public CreateBookingRequest() {
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
}