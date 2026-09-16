package com.fabhotels.dto.response;

import java.util.List;

public class HotelSearchPageResponse {

    private List<HotelSearchResponse> content;
    private int page;
    private int size;
    private long totalElements;
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