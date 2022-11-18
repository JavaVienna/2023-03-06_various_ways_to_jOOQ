package at.softwarecraftsmen.persistenceshowcase.persistence;

import java.util.List;

public interface CustomAlbumRepository {

    List<String> findAlbumsRankedByDistinctTaggedPersonsWithJooq();
}
