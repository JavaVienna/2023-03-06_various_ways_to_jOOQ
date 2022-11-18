package at.softwarecraftsmen.persistenceshowcase.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Data
@NoArgsConstructor

@MappedSuperclass
public abstract class AbstractPerson extends AbstractPersistable<Long> {

    @NotNull
    @Version
    private Integer version;

    @Email
    @NotNull
    @Column(length = 64)
    private String userName;

    @NotNull
    @Column(length = 64)
    private String firstName;

    @NotNull
    @Column(length = 64)
    private String lastName;

    public AbstractPerson(String userName, String firstName, String lastName) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
