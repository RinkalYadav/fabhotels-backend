package com.fabhotels.service;

import com.fabhotels.dto.request.CreateRoomRequest;
import com.fabhotels.dto.response.RoomResponse;
import com.fabhotels.entity.Hotel;
import com.fabhotels.entity.Room;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import com.fabhotels.exception.DuplicateRoomException;
import com.fabhotels.exception.HotelNotFoundException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.HotelRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Hotel hotel;
    private CreateRoomRequest request;

    @BeforeEach
    void setUp() {

        hotel = new Hotel();
        hotel.setId(10L);

        request = new CreateRoomRequest();

        request.setRoomNumber("101");
        request.setRoomType(RoomType.DELUXE);
        request.setPricePerNight(
                new BigDecimal("3500.00")
        );
        request.setCapacity(2);
    }

    @Test
    void createRoom_whenHotelExists_shouldCreateRoom() {

        when(hotelRepository.findById(10L))
                .thenReturn(Optional.of(hotel));

        when(
                roomRepository.existsByHotelIdAndRoomNumber(
                        10L,
                        "101"
                )
        ).thenReturn(false);

        Room savedRoom = new Room();

        savedRoom.setId(1L);
        savedRoom.setHotel(hotel);
        savedRoom.setRoomNumber("101");
        savedRoom.setRoomType(RoomType.DELUXE);
        savedRoom.setPricePerNight(
                new BigDecimal("3500.00")
        );
        savedRoom.setCapacity(2);
        savedRoom.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.save(any(Room.class)))
                .thenReturn(savedRoom);

        RoomResponse response =
                roomService.createRoom(10L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getHotelId());
        assertEquals("101", response.getRoomNumber());
        assertEquals(RoomType.DELUXE, response.getRoomType());
        assertEquals(
                new BigDecimal("3500.00"),
                response.getPricePerNight()
        );
        assertEquals(2, response.getCapacity());
        assertEquals(
                RoomStatus.AVAILABLE,
                response.getStatus()
        );

        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void createRoom_whenHotelDoesNotExist_shouldThrowException() {

        when(hotelRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                HotelNotFoundException.class,
                () -> roomService.createRoom(999L, request)
        );

        verify(roomRepository, never())
                .save(any(Room.class));
    }

    @Test
    void createRoom_whenRoomAlreadyExists_shouldThrowException() {

        when(hotelRepository.findById(10L))
                .thenReturn(Optional.of(hotel));

        when(
                roomRepository.existsByHotelIdAndRoomNumber(
                        10L,
                        "101"
                )
        ).thenReturn(true);

        assertThrows(
                DuplicateRoomException.class,
                () -> roomService.createRoom(10L, request)
        );

        verify(roomRepository, never())
                .save(any(Room.class));
    }

    @Test
    void getRoomById_whenRoomExists_shouldReturnRoom() {

        Room room = new Room();

        room.setId(1L);
        room.setHotel(hotel);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPricePerNight(
                new BigDecimal("3500.00")
        );
        room.setCapacity(2);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        RoomResponse response =
                roomService.getRoomById(1L);

        assertEquals(1L, response.getId());
        assertEquals("101", response.getRoomNumber());
    }

    @Test
    void getRoomById_whenRoomDoesNotExist_shouldThrowException() {

        when(roomRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RoomNotFoundException.class,
                () -> roomService.getRoomById(999L)
        );
    }

    @Test
    void getRoomsByHotelId_whenHotelExists_shouldReturnRooms() {

        when(hotelRepository.existsById(10L))
                .thenReturn(true);

        Room room = new Room();

        room.setId(1L);
        room.setHotel(hotel);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPricePerNight(
                new BigDecimal("3500.00")
        );
        room.setCapacity(2);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.findByHotelId(10L))
                .thenReturn(List.of(room));

        List<RoomResponse> response =
                roomService.getRoomsByHotelId(10L);

        assertEquals(1, response.size());
        assertEquals("101", response.get(0).getRoomNumber());
    }

    @Test
    void getRoomsByHotelId_whenHotelDoesNotExist_shouldThrowException() {

        when(hotelRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                HotelNotFoundException.class,
                () -> roomService.getRoomsByHotelId(999L)
        );

        verify(roomRepository, never())
                .findByHotelId(anyLong());
    }
}