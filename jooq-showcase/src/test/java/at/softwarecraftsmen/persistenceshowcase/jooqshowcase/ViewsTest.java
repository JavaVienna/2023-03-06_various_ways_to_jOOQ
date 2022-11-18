package at.softwarecraftsmen.persistenceshowcase.jooqshowcase;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static at.softwarecraftsmen.jooqshowcase.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgresJooqExtension.class)
class ViewsTest {

    @Test
    void read_from_view(DSLContext dsl) {
        var photographerId = 1L;
        var photographer = dsl.newRecord(PHOTOGRAPHERS)
            .setId(photographerId)
            .setFirstName("Johnny")
            .setLastName("de Photographer")
            .setUserName("johnny.dephotographer@example.com")
            .setVersion(1);
        photographer.insert();

        var photoId = 5678L;
        var photo = dsl.newRecord(PHOTOS)
            .setId(photoId)
            .setPhotoName("Sunset in Paris")
            .setFileName("paris_sunset.jpg")
            .setPhotographerId(photographerId)
            .setWidth(2048)
            .setHeight(1024)
            .setOrientation("L")
            .setVersion(1);
        photo.insert();

        var photoDetails = dsl
            .selectFrom(PHOTO_DETAILS)
            .where(PHOTO_DETAILS.ID.eq(photoId))
            .fetchSingle();

        assertThat(photoDetails.getPhotoName()).isEqualTo("Sunset in Paris");
        assertThat(photoDetails.getPhotographerName()).isEqualTo("Johnny de Photographer");
        assertThat(photoDetails.getTaggedPeople()).isEmpty();
    }
}
