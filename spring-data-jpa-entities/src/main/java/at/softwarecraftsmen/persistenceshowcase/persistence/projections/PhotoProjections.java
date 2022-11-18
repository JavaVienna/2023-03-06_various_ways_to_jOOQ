package at.softwarecraftsmen.persistenceshowcase.persistence.projections;

import at.softwarecraftsmen.persistenceshowcase.entity.Orientation;

import java.time.LocalDateTime;

public class PhotoProjections {

    public record PhotoInfo(String name,
                            String fileName,
                            LocalDateTime creationTS,
                            Orientation orientation) {
    }
}
