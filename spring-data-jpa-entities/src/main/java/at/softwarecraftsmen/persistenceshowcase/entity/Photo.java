package at.softwarecraftsmen.persistenceshowcase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "photos", indexes = {
    @Index(name = "ix_photos_photo_name", columnList = "photo_name")
})
public class Photo extends AbstractPersistable<Long> {

    public static final int KEY_LENGTH = 4;

    @NotNull
    @Version
    private Integer version;

    @NotNull
    @NotBlank
    @Column(length = 64)
    private String fileName;

    @NotNull
    @NotBlank
    @Column(name = "photo_name", length = 64)
    private String name;

    @NotNull
    @PastOrPresent
    @Column(name = "creation_ts")
    private LocalDateTime creationTS;

    @NotNull
    @Min(0) @Max(4096)
    private Integer width;

    @NotNull
    @Min(0) @Max(4096)
    private Integer height;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_photo_photographer"))
    private Photographer photographer;

    @NotNull
    @Column(length = 1)
    private Orientation orientation;

    @Builder.Default
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "photo", fetch = FetchType.EAGER)
    private List<TaggedPerson> taggedPersons = new ArrayList<>(2);

    public Photo addTaggedPerson(Person person, int rating) {
        var taggedPerson = new TaggedPerson(person, this, rating);
        this.taggedPersons.add(taggedPerson);
        return this;
    }
}
