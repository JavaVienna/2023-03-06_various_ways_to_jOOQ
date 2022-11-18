package at.softwarecraftsmen.persistenceshowcase.entity;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

@Embeddable
public class Address {

    @NotBlank
    @Column(length = 64)
    private String streetNumber;

    @NotBlank
    @Column(length = 16)
    private String zipCode;

    @NotBlank
    @Column(length = 64)
    private String city;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn
    private Country country;
}
