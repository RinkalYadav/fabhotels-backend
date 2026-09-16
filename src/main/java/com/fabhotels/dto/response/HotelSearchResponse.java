package com.fabhotels.dto.response;

public class HotelSearchResponse {

    private Long id;
    private String name;
    private String city;
    private String state;
    private String country;
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