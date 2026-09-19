package com.fabhotels.security;

import com.fabhotels.entity.Booking;
import com.fabhotels.repository.BookingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("bookingAuthorization")
public class BookingAuthorizationService {

    private final BookingRepository bookingRepository;

    public BookingAuthorizationService(
            BookingRepository bookingRepository
    ) {
        this.bookingRepository = bookingRepository;
    }

    public boolean isOwner(
            Long bookingId
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return false;
        }

        String currentEmail =
                authentication.getName();

        return bookingRepository
                .findById(bookingId)
                .map(Booking::getGuestEmail)
                .map(email ->
                        email.equalsIgnoreCase(
                                currentEmail
                        )
                )
                .orElse(false);
    }
}