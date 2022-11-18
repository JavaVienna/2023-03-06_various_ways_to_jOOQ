package at.softwarecraftsmen.persistenceshowcase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public class AlbumPhoto {

    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_album_photos_2_photo"))
    private Photo photo;

    @PastOrPresent
    @NotNull
    @Column(name = "assignment_ts", nullable = false)
    private LocalDateTime assignmentTS;
}
