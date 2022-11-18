package at.softwarecraftsmen.persistenceshowcase.jooqshowcase;

import at.softwarecraftsmen.jooqshowcase.Routines;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static at.softwarecraftsmen.jooqshowcase.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgresJooqExtension.class)
class RoutinesTest {

    @Test
    void call_stored_procedure(DSLContext dsl) {
        var personId = 1234L;
        var photoId = 1L;
        var person = dsl.newRecord(PERSONS)
            .setId(personId)
            .setFirstName("Alice")
            .setLastName("Example")
            .setUserName("alice@example.com")
            .setNickName("alice1")
            .setVersion(1);
        person.insert();

        var photo = dsl.newRecord(PHOTOS)
            .setId(photoId)
            .setPhotoName("My Favorite Photo")
            .setFileName("mfp.jpg")
            .setWidth(1024)
            .setHeight(2048)
            .setOrientation("P")
            .setVersion(1);
        photo.insert();

        var taggedPerson = dsl.newRecord(TAGGED_PERSONS)
            .setId(1L)
            .setPersonId(personId)
            .setPhotoId(1L)
            .setRating(10);
        taggedPerson.insert();

        var ratingStatistics = Routines.ratingStatisticsForPerson(dsl.configuration(), personId);

        assertThat(ratingStatistics.getAvgRatingInGoodPhotos()).isEqualTo(10);
        assertThat(ratingStatistics.getNumberOfGoodPhotos()).isEqualTo(1);
    }
}
