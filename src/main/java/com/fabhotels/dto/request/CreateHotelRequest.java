package com.fabhotels.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating a hotel")
public class CreateHotelRequest {

    @NotBlank
    @Size(max = 100)
    @Schema(
            description = "Hotel name",
            example = "FabHotel Bangalore Central",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Size(max = 500)
    @Schema(
            description = "Hotel description",
            example = "Comfortable hotel located in central Bangalore",
            maxLength = 500
    )
    private String description;

    @NotBlank
    @Size(max = 255)
    @Schema(
            description = "Hotel address",
            example = "123 MG Road, Bangalore",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String address;

    @NotBlank
    @Size(max = 100)
    @Schema(
            description = "City where the hotel is located",
            example = "Bangalore",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String city;

    @NotBlank
    @Size(max = 100)
    @Schema(
            description = "State where the hotel is located",
            example = "Karnataka",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String state;

    @NotBlank
    @Size(max = 100)
    @Schema(
            description = "Country where the hotel is located",
            example = "India",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String country;

    @NotBlank
    @Pattern(
            regexp = "\\d{6}",
            message = "Pincode must contain exactly 6 digits"
    )
    @Schema(
            description = "Six-digit postal pincode",
            example = "560001",
            pattern = "\\d{6}",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String pincode;
}