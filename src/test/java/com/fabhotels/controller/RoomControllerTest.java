package com.fabhotels.controller;

import tools.jackson.databind.ObjectMapper;
import com.fabhotels.dto.request.CreateRoomRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.dto.response.RoomResponse;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import com.fabhotels.service.BookingService;
import com.fabhotels.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private BookingService bookingService;

    private RoomResponse response() {

        return new RoomResponse(
                10L,
                1L,
                "101",
                RoomType.DOUBLE,
                new BigDecimal("2500.00"),
                2,
                RoomStatus.AVAILABLE
        );
    }

    @Test
    void createRoom_shouldReturn201()
            throws Exception {

        CreateRoomRequest request =
                new CreateRoomRequest();

        request.setRoomNumber("101");
        request.setRoomType(RoomType.DOUBLE);
        request.setPricePerNight(
                new BigDecimal("2500")
        );
        request.setCapacity(2);

        when(
                roomService.createRoom(
                        any(Long.class),
                        any(CreateRoomRequest.class)
                )
        ).thenReturn(response());

        mockMvc.perform(
                        post("/api/hotels/1/rooms")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.hotelId").value(1))
                .andExpect(jsonPath("$.roomNumber")
                        .value("101"))
                .andExpect(jsonPath("$.roomType")
                        .value("DOUBLE"))
                .andExpect(jsonPath("$.pricePerNight")
                        .value(2500))
                .andExpect(jsonPath("$.capacity")
                        .value(2))
                .andExpect(jsonPath("$.status")
                        .value("AVAILABLE"));
    }

    @Test
    void createRoom_withInvalidCapacity_shouldReturn400()
            throws Exception {

        CreateRoomRequest request =
                new CreateRoomRequest();

        request.setRoomNumber("101");
        request.setRoomType(RoomType.DOUBLE);
        request.setPricePerNight(
                new BigDecimal("2500")
        );
        request.setCapacity(0);

        mockMvc.perform(
                        post("/api/hotels/1/rooms")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRoomById_shouldReturn200()
            throws Exception {

        when(
                roomService.getRoomById(10L)
        ).thenReturn(response());

        mockMvc.perform(
                        get("/api/rooms/10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.roomNumber")
                        .value("101"));
    }

    @Test
    void getRoomsByHotelId_shouldReturnList()
            throws Exception {

        when(
                roomService.getRoomsByHotelId(1L)
        ).thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/hotels/1/rooms")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id")
                        .value(10))
                .andExpect(jsonPath("$[0].roomNumber")
                        .value("101"));
    }

    @Test
    void getBookingsByRoom_shouldReturnList()
            throws Exception {

        BookingResponse booking =
                new BookingResponse(
                        20L,
                        10L,
                        "Rinkal",
                        "a@b.com",
                        LocalDate.of(2030, 1, 1),
                        LocalDate.of(2030, 1, 3),
                        2,
                        new BigDecimal("5000"),
                        BookingStatus.CONFIRMED,
                        LocalDateTime.now()
                );

        when(
                bookingService.getBookingsByRoom(10L)
        ).thenReturn(List.of(booking));

        mockMvc.perform(
                        get("/api/10/bookings")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(20))
                .andExpect(jsonPath("$[0].roomId")
                        .value(10));
    }
}