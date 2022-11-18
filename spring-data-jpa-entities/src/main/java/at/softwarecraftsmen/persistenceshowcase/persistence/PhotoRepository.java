package at.softwarecraftsmen.persistenceshowcase.persistence;

import at.softwarecraftsmen.persistenceshowcase.entity.Photo;
import at.softwarecraftsmen.persistenceshowcase.entity.Photographer;
import at.softwarecraftsmen.persistenceshowcase.persistence.projections.PhotoProjections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findAllByNameLike(String name);

    List<Photo> findAllByCreationTSBetween(LocalDateTime startTS, LocalDateTime endTS);

    List<PhotoProjections.PhotoInfo> findAllByPhotographer(Photographer photographer);
}