package at.softwarecraftsmen.persistenceshowcase.persistence;

import at.softwarecraftsmen.persistenceshowcase.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, CustomAlbumRepository {

    List<Album> findAllByNameLike(String name);

    List<Album> findAllByCreationTSBetween(LocalDateTime startTS, LocalDateTime endTS);

    @Query(
        value = """
            select albums.album_name
            from albums
            join album_photos
            on albums.id = album_photos.album_id
            join photos
            on album_photos.photo_id = photos.id
            join tagged_persons
            on photos.id = tagged_persons.photo_id
            group by albums.id, albums.album_name
            order by count(distinct tagged_persons.person_id) desc
        """,
        nativeQuery = true
    )
    List<String> findAlbumsRankedByDistinctTaggedPersonsWithNativeQuery();
}
