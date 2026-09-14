package com.fabhotels.repository;

import com.fabhotels.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
