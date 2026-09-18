package com.fabhotels.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing hotel details")
public class HotelResponse {

    @Schema(
            description = "Unique hotel ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Hotel name",
            example = "FabHotel Bangalore Central"
    )
    private String name;

    @Schema(
            description = "Hotel description",
            example = "Comfortable hotel located in central Bangalore"
    )
    private String description;

    @Schema(
            description = "Hotel address",
            example = "123 MG Road, Bangalore"
    )
    private String address;

    @Schema(
            description = "Hotel city",
            example = "Bangalore"
    )
    private String city;

    @Schema(
            description = "Hotel state",
            example = "Karnataka"
    )
    private String state;

    @Schema(
            description = "Hotel country",
            example = "India"
    )
    private String country;

    @Schema(
            description = "Hotel postal pincode",
            example = "560001"
    )
    private String pincode;

    @Schema(
            description = "Indicates whether the hotel is active",
            example = "true"
    )
    private boolean active;
}