package com.fabhotels.controller;

import tools.jackson.databind.ObjectMapper;
import com.fabhotels.dto.request.CreateHotelRequest;
import com.fabhotels.dto.response.HotelResponse;
import com.fabhotels.dto.response.HotelSearchPageResponse;
import com.fabhotels.dto.response.HotelSearchResponse;
import com.fabhotels.service.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HotelService hotelService;

    @Test
    void createHotel_shouldReturn201()
            throws Exception {

        CreateHotelRequest request =
                new CreateHotelRequest(
                        "Fab Hotel",
                        "Nice hotel",
                        "MG Road",
                        "Bengaluru",
                        "Karnataka",
                        "India",
                        "560001"
                );

        HotelResponse response =
                new HotelResponse();

        response.setId(1L);
        response.setName("Fab Hotel");
        response.setCity("Bengaluru");

        when(
                hotelService.createHotel(
                        any(CreateHotelRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/hotels")
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
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Fab Hotel"));
    }

    @Test
    void createHotel_withInvalidPincode_shouldReturn400()
            throws Exception {

        CreateHotelRequest request =
                new CreateHotelRequest(
                        "Fab Hotel",
                        "Nice hotel",
                        "MG Road",
                        "Bengaluru",
                        "Karnataka",
                        "India",
                        "123"
                );

        mockMvc.perform(
                        post("/api/hotels")
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
    void getHotelById_shouldReturn200()
            throws Exception {

        HotelResponse response =
                new HotelResponse();

        response.setId(1L);
        response.setName("Fab Hotel");

        when(
                hotelService.getHotelById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/hotels/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Fab Hotel"));
    }

    @Test
    void getAllHotels_shouldReturn200()
            throws Exception {

        HotelResponse response =
                new HotelResponse();

        response.setId(1L);
        response.setName("Fab Hotel");

        when(
                hotelService.getAllHotels()
        ).thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/hotels")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Fab Hotel"));
    }

    @Test
    void searchHotels_shouldReturn200()
            throws Exception {

        HotelSearchResponse item =
                new HotelSearchResponse(
                        1L,
                        "Fab Hotel",
                        "Bengaluru",
                        "Karnataka",
                        "India",
                        true
                );

        HotelSearchPageResponse response =
                new HotelSearchPageResponse(
                        List.of(item),
                        0,
                        10,
                        1,
                        1
                );

        when(
                hotelService.searchHotels(any())
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/hotels/search")
                                .param(
                                        "city",
                                        "Bengaluru"
                                )
                                .param(
                                        "active",
                                        "true"
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "10"
                                )
                                .param(
                                        "sortBy",
                                        "name"
                                )
                                .param(
                                        "sortDirection",
                                        "asc"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.content[0].name"
                ).value("Fab Hotel"))
                .andExpect(jsonPath(
                        "$.totalElements"
                ).value(1));
    }
}