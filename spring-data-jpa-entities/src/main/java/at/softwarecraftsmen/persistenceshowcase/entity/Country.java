package at.softwarecraftsmen.persistenceshowcase.entity;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

@Entity
@Table(name="countries")
public class Country extends AbstractPersistable<Integer> {

    @NotNull
    @Version
    private Integer version;

    @NotBlank
    @NotNull
    @Column(name = "name", length = 64)
    private String name;

    @NotBlank
    @NotNull
    @Column(length = 2)
    private String iso2Code;
}
