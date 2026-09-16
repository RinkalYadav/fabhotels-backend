package com.fabhotels.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 100)
    private String address;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @Size(max = 100)
    private String state;

    @NotBlank
    @Size(max = 100)
    private String country;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "Pincode must contain exactly 6 digits")
    private String pincode;

    private boolean active=true;

    @OneToMany(
            mappedBy = "hotel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Room> rooms = new ArrayList<>();

}
