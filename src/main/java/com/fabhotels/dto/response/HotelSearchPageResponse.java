package com.fabhotels.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated response containing hotel search results")
public class HotelSearchPageResponse {

    @Schema(description = "List of hotels matching the search criteria")
    private List<HotelSearchResponse> content;

    @Schema(description = "Current page number", example = "0")
    private int page;

    @Schema(description = "Number of records requested per page", example = "10")
    private int size;

    @Schema(description = "Total number of matching hotels", example = "25")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "3")
    private int totalPages;

    public HotelSearchPageResponse() {
    }

    public HotelSearchPageResponse(
            List<HotelSearchResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<HotelSearchResponse> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}