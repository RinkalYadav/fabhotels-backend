package com.fabhotels.repository;

import com.fabhotels.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);

    boolean existsByHotelIdAndRoomNumber(
            Long hotelId,
            String roomNumber
    );
}
