package com.fabhotels.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request parameters for searching and filtering hotels")
public class HotelSearchRequest {

    @Schema(
            description = "Filter hotels by city",
            example = "Bangalore"
    )
    private String city;

    @Schema(
            description = "Filter hotels by state",
            example = "Karnataka"
    )
    private String state;

    @Schema(
            description = "Filter hotels by active status",
            example = "true"
    )
    private Boolean active;

    @Schema(
            description = "Page number starting from 0",
            example = "0",
            defaultValue = "0",
            minimum = "0"
    )
    private int page = 0;

    @Schema(
            description = "Number of records per page",
            example = "10",
            defaultValue = "10",
            minimum = "1"
    )
    private int size = 10;

    @Schema(
            description = "Field used for sorting",
            example = "name",
            defaultValue = "id"
    )
    private String sortBy = "id";

    @Schema(
            description = "Sorting direction",
            example = "asc",
            defaultValue = "asc",
            allowableValues = {"asc", "desc"}
    )
    private String sortDirection = "asc";

    public HotelSearchRequest() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}