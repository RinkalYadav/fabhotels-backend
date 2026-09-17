package com.fabhotels.repository;

import com.fabhotels.entity.Hotel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HotelRepositoryTest {

    @Autowired
    private HotelRepository repository;

    private Hotel createHotel(
            String name,
            String city,
            String state,
            boolean active
    ) {
        Hotel hotel = new Hotel();

        hotel.setName(name);
        hotel.setDescription("Test hotel");
        hotel.setAddress("Main Road");
        hotel.setCity(city);
        hotel.setState(state);
        hotel.setCountry("India");
        hotel.setPincode("560001");
        hotel.setActive(active);

        return repository.save(hotel);
    }

    @Test
    void shouldFindHotelsByCity() {

        createHotel(
                "Fab Bangalore",
                "Bengaluru",
                "Karnataka",
                true
        );

        createHotel(
                "Fab Mumbai",
                "Mumbai",
                "Maharashtra",
                true
        );

        Specification<Hotel> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(
                                        root.get("city")
                                ),
                                "bengaluru"
                        );

        Page<Hotel> result =
                repository.findAll(
                        specification,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .get(0)
                        .getCity()
        ).isEqualTo("Bengaluru");
    }

    @Test
    void shouldFindHotelsByState() {

        createHotel(
                "Fab Bangalore",
                "Bengaluru",
                "Karnataka",
                true
        );

        createHotel(
                "Fab Mumbai",
                "Mumbai",
                "Maharashtra",
                true
        );

        Specification<Hotel> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(
                                        root.get("state")
                                ),
                                "karnataka"
                        );

        Page<Hotel> result =
                repository.findAll(
                        specification,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .get(0)
                        .getState()
        ).isEqualTo("Karnataka");
    }

    @Test
    void shouldFindOnlyActiveHotels() {

        createHotel(
                "Active Hotel",
                "Bengaluru",
                "Karnataka",
                true
        );

        createHotel(
                "Inactive Hotel",
                "Bengaluru",
                "Karnataka",
                false
        );

        Specification<Hotel> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("active"),
                                true
                        );

        Page<Hotel> result =
                repository.findAll(
                        specification,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .get(0)
                        .isActive()
        ).isTrue();
    }

    @Test
    void shouldFilterByCityAndStateAndActive() {

        createHotel(
                "Bangalore Active",
                "Bengaluru",
                "Karnataka",
                true
        );

        createHotel(
                "Bangalore Inactive",
                "Bengaluru",
                "Karnataka",
                false
        );

        createHotel(
                "Mumbai Active",
                "Mumbai",
                "Maharashtra",
                true
        );

        Specification<Hotel> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.and(
                                criteriaBuilder.equal(
                                        criteriaBuilder.lower(
                                                root.get("city")
                                        ),
                                        "bengaluru"
                                ),
                                criteriaBuilder.equal(
                                        criteriaBuilder.lower(
                                                root.get("state")
                                        ),
                                        "karnataka"
                                ),
                                criteriaBuilder.equal(
                                        root.get("active"),
                                        true
                                )
                        );

        Page<Hotel> result =
                repository.findAll(
                        specification,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .get(0)
                        .getName()
        ).isEqualTo("Bangalore Active");
    }

    @Test
    void paginationAndSorting_shouldWork() {

        createHotel(
                "Zebra Hotel",
                "Bengaluru",
                "Karnataka",
                true
        );

        createHotel(
                "Alpha Hotel",
                "Bengaluru",
                "Karnataka",
                true
        );

        /*
         * IMPORTANT:
         * Do not use:
         *
         * repository.findAll(
         *     null,
         *     PageRequest.of(...)
         * );
         *
         * because JpaRepository has multiple findAll
         * overloads and null becomes ambiguous.
         *
         * For testing normal pagination + sorting,
         * use the one-argument findAll(Pageable).
         */

        Page<Hotel> result =
                repository.findAll(
                        PageRequest.of(
                                0,
                                1,
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "name"
                                )
                        )
                );

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .get(0)
                        .getName()
        ).isEqualTo("Alpha Hotel");

        assertThat(
                result.getTotalElements()
        ).isEqualTo(2);

        assertThat(
                result.getTotalPages()
        ).isEqualTo(2);
    }

    @Test
    void pagination_shouldReturnCorrectPage() {

        createHotel(
                "Hotel A",
                "Bengaluru",
                "Karnataka",
                true
        );

        createHotel(
                "Hotel B",
                "Bengaluru",
                "Karnataka",
                true
        );

        createHotel(
                "Hotel C",
                "Bengaluru",
                "Karnataka",
                true
        );

        Page<Hotel> result =
                repository.findAll(
                        PageRequest.of(
                                1,
                                2,
                                Sort.by("name").ascending()
                        )
                );

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .get(0)
                        .getName()
        ).isEqualTo("Hotel C");
    }
}