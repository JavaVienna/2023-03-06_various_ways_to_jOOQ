package at.softwarecraftsmen.persistenceshowcase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public class EmailAddress {

    @NotBlank
    @NotNull
    @Email
    @Column(name = "email_address", length = 64, nullable = false)
    private String value;
}
