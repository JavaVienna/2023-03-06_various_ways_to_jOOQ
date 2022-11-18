package at.softwarecraftsmen.persistenceshowcase.jooqshowcase;

import at.softwarecraftsmen.jooqshowcase.tables.daos.PhotosDao;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static at.softwarecraftsmen.jooqshowcase.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgresJooqExtension.class)
class PojosAndDaosTest {

    @Test
    void working_with_pojos_and_daos(DSLContext dsl) {
        var favoritePhotographerId = 1234L;
        var photographer = dsl.newRecord(PHOTOGRAPHERS)
            .setId(favoritePhotographerId)
            .setFirstName("Alice")
            .setLastName("McSnapshot")
            .setUserName("ams@example.com")
            .setVersion(1);
        photographer.insert();

        var photoId = 1L;
        var samplePhoto = dsl.newRecord(PHOTOS)
            .setId(photoId)
            .setPhotoName("Clouds over Tokyo")
            .setFileName("cot.jpg")
            .setPhotographerId(favoritePhotographerId)
            .setWidth(1024)
            .setHeight(2048)
            .setOrientation("P")
            .setVersion(1);
        samplePhoto.insert();

        var photoDao = new PhotosDao(dsl.configuration());
        var photos = photoDao.fetchByPhotographerId(favoritePhotographerId);
        photos.forEach(photo -> photo.setPhotoName(photo.getPhotoName() + " - Very cool!"));
        photoDao.update(photos);

        assertThat(photoDao.fetchOneById(photoId).getPhotoName())
            .isEqualTo("Clouds over Tokyo - Very cool!");
    }
}
