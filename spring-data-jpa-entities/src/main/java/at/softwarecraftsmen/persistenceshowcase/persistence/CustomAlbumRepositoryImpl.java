package at.softwarecraftsmen.persistenceshowcase.persistence;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static at.softwarecraftsmen.jooqshowcase.tables.AlbumPhotos.ALBUM_PHOTOS;
import static at.softwarecraftsmen.jooqshowcase.tables.Albums.ALBUMS;
import static at.softwarecraftsmen.jooqshowcase.tables.Photos.PHOTOS;
import static at.softwarecraftsmen.jooqshowcase.tables.TaggedPersons.TAGGED_PERSONS;
import static org.jooq.impl.DSL.countDistinct;

@Component
public class CustomAlbumRepositoryImpl implements CustomAlbumRepository {

    private final DSLContext dsl;

    public CustomAlbumRepositoryImpl(DSLContext dsl) { this.dsl = dsl; }

    @Override
    public List<String> findAlbumsRankedByDistinctTaggedPersonsWithJooq() {
        return dsl.select(ALBUMS.ID, ALBUMS.ALBUM_NAME, countDistinct(TAGGED_PERSONS.PERSON_ID))
            .from(ALBUMS)
            .join(ALBUM_PHOTOS)
            .on(ALBUMS.ID.eq(ALBUM_PHOTOS.ALBUM_ID))
            .join(PHOTOS)
            .on(ALBUM_PHOTOS.PHOTO_ID.eq(PHOTOS.ID))
            .join(TAGGED_PERSONS)
            .on(PHOTOS.ID.eq(TAGGED_PERSONS.PHOTO_ID))
            .groupBy(ALBUMS.ID, ALBUMS.ALBUM_NAME)
            .orderBy(countDistinct(TAGGED_PERSONS.PERSON_ID).desc())
            .fetch(result -> result.get(ALBUMS.ALBUM_NAME));
    }
}
