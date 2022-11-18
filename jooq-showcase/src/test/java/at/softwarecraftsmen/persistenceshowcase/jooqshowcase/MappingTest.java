package at.softwarecraftsmen.persistenceshowcase.jooqshowcase;

import org.jooq.DSLContext;
import org.jooq.Records;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static at.softwarecraftsmen.jooqshowcase.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgresJooqExtension.class)
class MappingTest {

    @Test
    void mapping_to_custom_types(DSLContext dsl) {
        var photographerId = 1L;
        var photographer = dsl.newRecord(PHOTOGRAPHERS)
            .setId(photographerId)
            .setFirstName("Example")
            .setLastName("Photographer")
            .setUserName("ep@example.com")
            .setVersion(1);
        photographer.insert();

        var samplePhoto = dsl.newRecord(PHOTOS)
            .setId(photographerId)
            .setPhotoName("Rome in the evening")
            .setFileName("rite.jpg")
            .setPhotographerId(photographerId)
            .setWidth(1024)
            .setHeight(2048)
            .setOrientation("P")
            .setVersion(1);
        samplePhoto.insert();

        var mappedPhotos = dsl.selectFrom(PHOTOS)
            .where(PHOTOS.PHOTOGRAPHER_ID.in(1L, 2L, 3L, 4L))
            .fetch(photo -> new MyPhotoDomainEntity(photo.getId(), photo.getPhotoName()));

        assertThat(mappedPhotos.get(0).getName()).isEqualTo("Rome in the evening");

        var mappedPhotosWithProjection = dsl.select(PHOTOS.ID, PHOTOS.PHOTO_NAME)
            .from(PHOTOS)
            .where(PHOTOS.PHOTOGRAPHER_ID.in(1L, 2L, 3L, 4L))
            .fetch(Records.mapping(MyPhotoDomainEntity::new));

        assertThat(mappedPhotosWithProjection.get(0).getName()).isEqualTo("Rome in the evening");
    }

    @Test
    void mapping_from_domain_event_to_update(DSLContext dsl) {
        var photographerId = 123L;
        var photographer = dsl.newRecord(PHOTOGRAPHERS)
            .setId(photographerId)
            .setFirstName("Photographer")
            .setLastName("McPhoto")
            .setUserName("pmp@example.com")
            .setVersion(1);
        photographer.insert();

        var oldEmail = dsl.newRecord(PHOTOGRAPHER_EMAILS)
            .setPhotographerId(photographerId)
            .setEmailAddress("the_old_email@example.com")
            .setEmailType("B");
        oldEmail.insert();

        var newPhotographerEmail = "the.photographer@example.com";
        var event = new PhotographerBusinessEmailChanged(photographerId, newPhotographerEmail);

        dsl.update(PHOTOGRAPHER_EMAILS)
            .set(PHOTOGRAPHER_EMAILS.EMAIL_ADDRESS, event.newEmail)
            .where(PHOTOGRAPHER_EMAILS.PHOTOGRAPHER_ID.eq(event.photographerId))
            .and(PHOTOGRAPHER_EMAILS.EMAIL_TYPE.eq("B"))
            .execute();

        assertThat(dsl.selectFrom(PHOTOGRAPHER_EMAILS).fetchSingle().getEmailAddress())
            .isEqualTo("the.photographer@example.com");
    }

    static class MyPhotoDomainEntity {
        private final long id;
        private final String name;

        MyPhotoDomainEntity(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    record PhotographerBusinessEmailChanged(long photographerId, String newEmail) {
    }
}
