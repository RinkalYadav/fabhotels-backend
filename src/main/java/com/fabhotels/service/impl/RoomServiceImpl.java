package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CreateRoomRequest;
import com.fabhotels.dto.response.RoomResponse;
import com.fabhotels.entity.Hotel;
import com.fabhotels.entity.Room;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.exception.DuplicateRoomException;
import com.fabhotels.exception.HotelNotFoundException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.HotelRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.RoomService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomServiceImpl(
            RoomRepository roomRepository,
            HotelRepository hotelRepository
    ) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    /**
     * Create a new room.
     *
     * Only HOTEL_ADMIN users are allowed to create rooms.
     */
    @Override
    @PreAuthorize("hasRole('HOTEL_ADMIN')")
    public RoomResponse createRoom(
            Long hotelId,
            CreateRoomRequest request
    ) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(
                        () -> new HotelNotFoundException(hotelId)
                );

        boolean roomExists =
                roomRepository.existsByHotelIdAndRoomNumber(
                        hotelId,
                        request.getRoomNumber()
                );

        if (roomExists) {
            throw new DuplicateRoomException(
                    hotelId,
                    request.getRoomNumber()
            );
        }

        Room room = new Room();

        room.setHotel(hotel);
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setStatus(RoomStatus.AVAILABLE);

        Room savedRoom = roomRepository.save(room);

        return mapToResponse(savedRoom);
    }

    /**
     * Get room details.
     *
     * Both CUSTOMER and HOTEL_ADMIN can view room details.
     */
    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER', 'HOTEL_ADMIN')")
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(
                        () -> new RoomNotFoundException(roomId)
                );

        return mapToResponse(room);
    }

    /**
     * Get all rooms belonging to a hotel.
     *
     * Both CUSTOMER and HOTEL_ADMIN can view rooms.
     */
    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER', 'HOTEL_ADMIN')")
    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByHotelId(
            Long hotelId
    ) {

        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }

        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Convert Room entity to RoomResponse.
     */
    private RoomResponse mapToResponse(Room room) {

        return new RoomResponse(
                room.getId(),
                room.getHotel().getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.getCapacity(),
                room.getStatus()
        );
    }
}