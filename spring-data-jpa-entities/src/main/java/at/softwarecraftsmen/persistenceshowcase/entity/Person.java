package at.softwarecraftsmen.persistenceshowcase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor

@Entity
@Table(
    name = "persons",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_persons_nick_name", columnNames = "nick_name"),
    }
)
public class Person extends AbstractPerson {

    @NotBlank
    @NotNull
    @Size(min = 2, max = 64)
    @Column(name = "nick_name", length = 64, unique = true)
    private String nickName;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "person")
    private List<TaggedPerson> taggedPersons = new ArrayList<>(2);

    @Builder
    public Person(String userName, String firstName, String lastName, String nickName) {
        super(userName, firstName, lastName);
        this.nickName = nickName;
    }
}
