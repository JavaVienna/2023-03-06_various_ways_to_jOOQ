package at.softwarecraftsmen.persistenceshowcase.legacydb;

import at.softwarecraftsmen.jooqshowcase.tables.records.*;
import net.datafaker.Faker;
import org.jooq.DSLContext;
import org.jooq.TableRecord;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LegacyDataGenerator {

    private static final Locale LOCALE = Locale.forLanguageTag("de-AT");
    private static final long COUNTRY_ID_AUSTRIA = 1L;

    private final DSLContext sql;
    private final Faker faker;
    private Long id = 1L;

    public LegacyDataGenerator(DSLContext sql) {
        this.sql = sql;
        this.faker = new Faker(LOCALE);
    }

    public void fillWithTestData() {
        fillCountries();
        var persons = fillPersons();
        var photographers = fillPhotographers();
        var photos = fillPhotos(photographers);
        fillTaggedPersons(photos, persons);
        var albums = fillAlbums();
        fillAlbumPhotos(albums, photos);
    }

    private void fillCountries() {
        var countries = List.of(
            new CountriesRecord(COUNTRY_ID_AUSTRIA, 1, "AT", "Austria"),
            new CountriesRecord(2L, 1, "DE", "Germany"),
            new CountriesRecord(3L, 1, "GB", "United Kingdom")
        );

        sql.batchInsert(countries).execute();
    }

    private List<PersonsRecord> fillPersons() {
        return fill(this::generatePerson, 500);
    }

    private PersonsRecord generatePerson() {
        var person = new PersonsRecord();
        person.setId(nextId());
        person.setFirstName(faker.name().firstName());
        person.setLastName(faker.name().lastName());
        person.setUserName(faker.numerify(faker.pokemon().name().toLowerCase(LOCALE) + "#####"));
        person.setNickName(person.getUserName());
        person.setVersion(1);
        return person;
    }

    private List<PhotographersRecord> fillPhotographers() {
        return fill(this::generatePhotographer, 100);
    }

    private PhotographersRecord generatePhotographer() {
        var photographer = new PhotographersRecord();
        photographer.setId(nextId());
        photographer.setVersion(1);
        photographer.setFirstName(faker.name().firstName());
        photographer.setLastName(faker.name().lastName());
        photographer.setUserName(
            faker.numerify(
                faker.superhero().name().toLowerCase().replace(" ", "_") + "##"
            )
        );

        photographer.setBusinessCountryCode(43);
        photographer.setBusinessAreaCode(faker.number().numberBetween(0, 9999));
        photographer.setBusinessSerialNumber(faker.phoneNumber().phoneNumberNational());

        photographer.setMobileCountryCode(43);
        photographer.setMobileAreaCode(faker.number().numberBetween(0, 9999));
        photographer.setMobileSerialNumber(faker.phoneNumber().phoneNumberNational());

        photographer.setBillingStreetNumber(faker.address().buildingNumber());
        photographer.setBillingZipCode(faker.address().zipCode());
        photographer.setBillingCity(faker.address().city());
        photographer.setBillingCountryId(COUNTRY_ID_AUSTRIA);

        photographer.setStudioStreetNumber(faker.address().buildingNumber());
        photographer.setStudioZipCode(faker.address().zipCode());
        photographer.setStudioCity(faker.address().city());
        photographer.setStudioCountryId(COUNTRY_ID_AUSTRIA);

        return photographer;
    }

    private List<PhotosRecord> fillPhotos(List<PhotographersRecord> photographers) {
        return fill(() -> generatePhoto(photographers), 50_000);
    }

    private PhotosRecord generatePhoto(List<PhotographersRecord> photographers) {
        var photo = new PhotosRecord();
        photo.setId(nextId());
        photo.setVersion(1);
        photo.setPhotoName(
            anyOf(
                faker.food()::dish,
                faker.food()::ingredient,
                faker.food()::spice,
                faker.food()::dish,
                faker.food()::fruit,
                faker.food()::vegetable,
                faker.food()::sushi
            )
        );
        photo.setCreationTs(faker.date().past(10_000, TimeUnit.DAYS).toLocalDateTime());
        photo.setFileName(faker.file().fileName("", null, "jpg", ""));
        var width = faker.number().numberBetween(24, 2048);
        photo.setHeight(width);
        var height = faker.number().numberBetween(24, 2048);
        photo.setWidth(height);
        photo.setOrientation(width == height ? "S" : width > height ? "L" : "P");

        var photographer = randomItem(photographers);
        photo.setPhotographerId(photographer.getId());

        return photo;
    }

    private void fillTaggedPersons(List<PhotosRecord> photos, List<PersonsRecord> persons) {
        var taggedPersons = photos.stream().flatMap(photo -> {
            var numberOfTaggedPeople = faker.number().numberBetween(0, 8);
            return Stream
                .generate(() -> randomItem(persons))
                .distinct()
                .limit(numberOfTaggedPeople)
                .map(person -> {
                    var taggedPerson = new TaggedPersonsRecord();
                    var rating = faker.number().numberBetween(0, 11);

                    taggedPerson.setId(nextId());
                    taggedPerson.setPhotoId(photo.getId());
                    taggedPerson.setPersonId(person.getId());
                    taggedPerson.setRating(rating);

                    return taggedPerson;
                });
        }).toList();

        sql.batchInsert(taggedPersons).execute();
    }

    private List<AlbumsRecord> fillAlbums() {
        return fill(this::generateAlbum, 30);
    }

    private AlbumsRecord generateAlbum() {
        var album = new AlbumsRecord();
        album.setId(nextId());
        album.setVersion(1);
        album.setAlbumName(faker.funnyName().name());
        album.setCreationTs(faker.date().past(10_000, TimeUnit.DAYS).toLocalDateTime());
        album.setRestricted(faker.bool().bool());
        return album;
    }

    private List<AlbumPhotosRecord> fillAlbumPhotos(List<AlbumsRecord> albums, List<PhotosRecord> photos) {
        var albumPhotos = albums.stream()
            .flatMap(album -> {
                var numberOfPhotos = faker.number().numberBetween(0, 101);
                return generateAlbumPhotos(photos, album, numberOfPhotos).stream();
            })
            .toList();

        sql.batchInsert(albumPhotos).execute();

        return albumPhotos;
    }

    private List<AlbumPhotosRecord> generateAlbumPhotos(
        List<PhotosRecord> photos,
        AlbumsRecord album,
        int numberOfPhotos
    ) {
        var albumPhotos = Stream
            .generate(() -> randomItem(photos))
            .distinct()
            .limit(numberOfPhotos)
            .map(photo -> {
                var albumPhoto = new AlbumPhotosRecord();
                albumPhoto.setAlbumId(album.getId());
                albumPhoto.setPhotoId(photo.getId());
                albumPhoto.setAssignmentTs(faker.date().past(10_000, TimeUnit.DAYS).toLocalDateTime());
                return albumPhoto;
            })
            .toList();

        for (int i = 0; i < albumPhotos.size(); i++) {
            albumPhotos.get(i).setPosition(i);
        }

        return albumPhotos;
    }

    private <R extends TableRecord<R>> List<R> fill(Supplier<R> generator, int numberOfRecords) {
        var generatedRecords = Stream
            .generate(generator)
            .limit(numberOfRecords)
            .toList();

        sql.batchInsert(generatedRecords).execute();

        return generatedRecords;
    }

    @SafeVarargs
    private String anyOf(Supplier<String>... suppliers) {
        var index = faker.number().numberBetween(0, suppliers.length);
        return suppliers[index].get();
    }

    private <T> T randomItem(List<T> list) {
        return list.get(faker.number().numberBetween(0, list.size()));
    }

    private Long nextId() {
        return id++;
    }
}
