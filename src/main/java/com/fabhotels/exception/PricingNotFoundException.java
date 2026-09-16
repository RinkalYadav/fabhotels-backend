package com.fabhotels.exception;

public class PricingNotFoundException extends RuntimeException {

    public PricingNotFoundException(Long id) {
        super("Pricing not found with id: " + id);
    }
}