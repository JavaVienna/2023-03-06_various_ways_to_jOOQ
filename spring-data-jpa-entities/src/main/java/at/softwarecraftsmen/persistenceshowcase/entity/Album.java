package at.softwarecraftsmen.persistenceshowcase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "albums")
public class Album extends AbstractPersistable<Long> {

    @NotNull
    @Version
    private Integer version;

    @NotBlank
    @NotNull
    @Column(name = "album_name", length = 64)
    private String name;

    @PastOrPresent
    @NotNull
    @Column(name = "creation_ts")
    private LocalDateTime creationTS;

    @NotNull
    private boolean restricted;

    @Builder.Default
    @ElementCollection
    @CollectionTable(
        name = "album_photos",
        joinColumns = @JoinColumn(name = "album_id", foreignKey = @ForeignKey(name = "fk_album_photos_2_album"))
    )
    @OrderColumn(name = "position")
    private List<AlbumPhoto> photos = new ArrayList<>();

    public Album addPhotos(LocalDateTime assignmentTS, Photo... photos) {
        Arrays.stream(photos)
            .filter(Objects::nonNull)
            .map(p -> createAlbumPhoto(assignmentTS, p))
            .forEach(this.photos::add);
        return this;
    }

    private AlbumPhoto createAlbumPhoto(LocalDateTime assignmentTS, Photo photo) {
        return AlbumPhoto.builder()
            .photo(photo)
            .assignmentTS(assignmentTS)
            .build();
    }
}
