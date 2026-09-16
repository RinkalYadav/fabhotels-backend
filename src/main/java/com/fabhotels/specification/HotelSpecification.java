package com.fabhotels.specification;

import com.fabhotels.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;

public final class HotelSpecification {

    private HotelSpecification() {
    }

    public static Specification<Hotel> hasCity(String city) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(
                                root.get("city")
                        ),
                        city.trim().toLowerCase()
                );
    }

    public static Specification<Hotel> hasState(String state) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(
                                root.get("state")
                        ),
                        state.trim().toLowerCase()
                );
    }

    public static Specification<Hotel> hasActive(Boolean active) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("active"),
                        active
                );
    }
}