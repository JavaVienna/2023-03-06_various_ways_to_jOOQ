package at.softwarecraftsmen.persistenceshowcase.jooqshowcase;

import at.softwarecraftsmen.jooqshowcase.tables.records.PhotosRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static at.softwarecraftsmen.jooqshowcase.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.*;

@ExtendWith(PostgresJooqExtension.class)
class QueriesTest {

    @Test
    void query_photos_from_austrian_studios(DSLContext dsl) {
        var austria = dsl.newRecord(COUNTRIES)
            .setId(1L)
            .setName("Austria")
            .setIso2code("AT")
            .setVersion(1);
        austria.insert();

        var italy = dsl.newRecord(COUNTRIES)
            .setId(2L)
            .setName("Italy")
            .setIso2code("IT")
            .setVersion(1);
        italy.insert();

        var austrianPhotographer = dsl.newRecord(PHOTOGRAPHERS)
            .setId(1L)
            .setStudioCountryId(austria.getId())
            .setFirstName("Austrian")
            .setLastName("Photographer")
            .setUserName("austrian.photographer@example.com")
            .setVersion(1);
        austrianPhotographer.insert();

        var italianPhotographer = dsl.newRecord(PHOTOGRAPHERS)
            .setId(2L)
            .setStudioCountryId(italy.getId())
            .setFirstName("Italian")
            .setLastName("Photographer")
            .setUserName("italian.photographer@example.com")
            .setVersion(1);
        italianPhotographer.insert();

        var austrianPhoto = dsl.newRecord(PHOTOS)
            .setId(1L)
            .setPhotographerId(austrianPhotographer.getId())
            .setFileName("vienna.png")
            .setPhotoName("Vienna")
            .setWidth(60)
            .setHeight(60)
            .setOrientation("S")
            .setVersion(1);
        austrianPhoto.insert();

        var italianPhoto = dsl.newRecord(PHOTOS)
            .setId(2L)
            .setPhotographerId(italianPhotographer.getId())
            .setFileName("milano.png")
            .setPhotoName("Milano")
            .setWidth(60)
            .setHeight(60)
            .setOrientation("S")
            .setVersion(1);
        italianPhoto.insert();

        List<PhotosRecord> photosFromAustrianStudios = dsl
            .select(PHOTOS.fields())
            .from(PHOTOS)
            .join(PHOTOGRAPHERS)
            .on(PHOTOS.PHOTOGRAPHER_ID.eq(PHOTOGRAPHERS.ID))
            .join(COUNTRIES)
            .on(PHOTOGRAPHERS.STUDIO_COUNTRY_ID.eq(COUNTRIES.ID))
            .where(COUNTRIES.ISO2CODE.eq("AT"))
            .fetchInto(PHOTOS);

        assertThat(photosFromAustrianStudios).hasSize(1);
        assertThat(photosFromAustrianStudios.get(0).getPhotoName()).isEqualTo("Vienna");
    }

    @Test
    void rating_statistics_cte_example(DSLContext dsl) {
        var personId = 1234L;
        var person = dsl.newRecord(PERSONS)
            .setId(personId)
            .setFirstName("John")
            .setLastName("Example")
            .setUserName("john@example.com")
            .setNickName("john1")
            .setVersion(1);
        person.insert();

        var photo = dsl.newRecord(PHOTOS)
            .setId(1L)
            .setPhotoName("A very cool Photo")
            .setFileName("photo.jpg")
            .setWidth(1024)
            .setHeight(2048)
            .setOrientation("P")
            .setVersion(1);
        photo.insert();

        var taggedPerson = dsl.newRecord(TAGGED_PERSONS)
            .setId(1L)
            .setPersonId(personId)
            .setPhotoId(1L)
            .setRating(9);
        taggedPerson.insert();

        var photoId = name("photo_id");
        var personRating = name("person_rating");
        var avgRating = name("avg_rating");
        var avgRatingInGoodPhotos = field("avg_rating_in_good_photos", Integer.class);
        var numberOfGoodPhotos = field("get_number_of_good_photos", Integer.class);

        var photosWherePersonIsTagged = name("photos_where_person_is_tagged")
            .fields(photoId, personRating)
            .as(
                select(TAGGED_PERSONS.PHOTO_ID.as(photoId), TAGGED_PERSONS.RATING.as(personRating))
                    .from(TAGGED_PERSONS)
                    .where(TAGGED_PERSONS.PERSON_ID.eq(personId))
            );

        var ratingsOfPhotosWherePersonIsTagged = name("ratings_of_photos_where_person_is_tagged")
            .fields(photoId, personRating, avgRating)
            .as(
                select(
                    photosWherePersonIsTagged.field(photoId),
                    photosWherePersonIsTagged.field(personRating),
                    avg(TAGGED_PERSONS.RATING)
                )
                .from(TAGGED_PERSONS)
                .join(photosWherePersonIsTagged)
                .on(photosWherePersonIsTagged.field(photoId, BIGINT).eq(TAGGED_PERSONS.PHOTO_ID))
                .groupBy(photosWherePersonIsTagged.field(photoId), photosWherePersonIsTagged.field(personRating))
            );

        var ratingStatistics = dsl
            .with(photosWherePersonIsTagged)
            .with(ratingsOfPhotosWherePersonIsTagged)
            .select(
                avg(ratingsOfPhotosWherePersonIsTagged.field(personRating, INTEGER))
                    .cast(avgRatingInGoodPhotos)
                    .as(avgRatingInGoodPhotos),
                count(ratingsOfPhotosWherePersonIsTagged.field(personRating))
                    .as(numberOfGoodPhotos)
            )
            .from(ratingsOfPhotosWherePersonIsTagged)
            .where(ratingsOfPhotosWherePersonIsTagged.field(avgRating, NUMERIC).ge(BigDecimal.valueOf(6)))
            .fetchSingle();

        assertThat(ratingStatistics.get(avgRatingInGoodPhotos)).isEqualTo(9);
        assertThat(ratingStatistics.get(numberOfGoodPhotos)).isEqualTo(1);
    }

    @ParameterizedTest(name = "when filter is {0}, we find {1} record(s)")
    @CsvSource({
        "UK,1",
        "AT,1",
        ",2"
    })
    void filtering_with_a_dynamic_query(String optionalCountryCode, int expectedCount, DSLContext dsl) {
        var countryCode = Optional.ofNullable(optionalCountryCode);
        var uk = dsl.newRecord(COUNTRIES)
            .setId(1L)
            .setName("United Kingdom")
            .setIso2code("UK")
            .setVersion(1);
        uk.insert();

        var austria = dsl.newRecord(COUNTRIES)
            .setId(2L)
            .setName("Austria")
            .setIso2code("AT")
            .setVersion(1);
        austria.insert();

        var filterCountryCode = countryCode
            .map(COUNTRIES.ISO2CODE::eq)
            .orElse(noCondition());

        var numberOfCountries = dsl
            .selectCount()
            .from(COUNTRIES)
            .where(filterCountryCode)
            .fetchSingle()
            .value1();

        assertThat(numberOfCountries).isEqualTo(expectedCount);
    }
}
