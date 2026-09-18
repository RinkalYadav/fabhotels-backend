package com.fabhotels.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hotel information returned in search results")
public class HotelSearchResponse {

    @Schema(description = "Unique hotel ID", example = "1")
    private Long id;

    @Schema(description = "Hotel name", example = "FabHotel Prime")
    private String name;

    @Schema(description = "City where the hotel is located", example = "Bangalore")
    private String city;

    @Schema(description = "State where the hotel is located", example = "Karnataka")
    private String state;

    @Schema(description = "Country where the hotel is located", example = "India")
    private String country;

    @Schema(description = "Whether the hotel is currently active", example = "true")
    private Boolean active;

    public HotelSearchResponse() {
    }

    public HotelSearchResponse(
            Long id,
            String name,
            String city,
            String state,
            String country,
            Boolean active
    ) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.country = country;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public Boolean getActive() {
        return active;
    }
}