package at.softwarecraftsmen.persistenceshowcase.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public class PhoneNumber {

    @Builder.Default
    @Min(0) @Max(999)
    private Integer countryCode = 43;

    @Min(0) @Max(99999)
    private Integer areaCode;

    @Size(min = 2, max = 16)
    private String serialNumber;
}
