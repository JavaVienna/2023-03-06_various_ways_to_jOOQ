package at.softwarecraftsmen.persistenceshowcase.persistence;

import at.softwarecraftsmen.persistenceshowcase.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
@Import(JooqConfiguration.class)
class AlbumRepositoryTest extends AbstractDataJpaTestcontainerTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    void ensureSavingAnAlbumWorks() {
        Country austria = Country.builder().name("Österreich").iso2Code("at").build();

        Address spg20 = Address.builder()
            .streetNumber("Spengergasse 20")
            .city("Vienna")
            .zipCode("1050")
            .country(austria)
            .build();

        Address billingAddress = Address.builder()
            .streetNumber("Spengergasse 21")
            .city("Vienna")
            .zipCode("1050")
            .country(austria)
            .build();

        Photographer uk = Photographer.builder()
            .firstName("Klaus")
            .lastName("UNGER")
            .userName("unger@spg.at")
            .studioAddress(spg20)
            .billingAddress(billingAddress)
            .mobileNumber(PhoneNumber.builder().areaCode(664).serialNumber("12341234").build())
            .businessNumber(PhoneNumber.builder().areaCode(664).serialNumber("43214321").build())
            .emails(List.of(
                    EmailAddress.builder().value("u@spg.at").build(),
                    EmailAddress.builder().value("uk@spg.at").build()))
            .build();

        Photo photo1 = Photo.builder()
            .fileName("DSC-4711.jpg")
            .name("Portrait of :)")
            .creationTS(LocalDateTime.now())
            .width(640)
            .height(480)
            .photographer(uk)
            .orientation(Orientation.LANDSCAPE)
            .build();

        Photo photo2 = Photo.builder()
            .fileName("DSC-4712.jpg")
            .name("Portrait of :(")
            .creationTS(LocalDateTime.now())
            .width(640)
            .height(480)
            .photographer(uk)
            .orientation(Orientation.LANDSCAPE)
            .build();

        Album album = Album.builder()
            .name("My first album")
            .creationTS(LocalDateTime.now())
            .restricted(false)
            .build()
            .addPhotos(LocalDateTime.now(), photo1, photo2);

        var saved = albumRepository.save(album);

        assertThat(saved).isSameAs(album);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();
    }

    @Test
    void customQueryWithJooq() {
        var now = LocalDateTime.now();

        Person alice = Person.builder()
            .firstName("Alice")
            .lastName("Example")
            .userName("alice1@example.com")
            .nickName("alice1")
            .build();

        Person bob = Person.builder()
            .firstName("Bob")
            .lastName("Example")
            .userName("bob1@example.com")
            .nickName("bob1")
            .build();

        Photo photo1OfAlbum1 = Photo.builder()
            .name("Photo 1 of Album 1")
            .fileName("P1A1.jpg")
            .width(100)
            .height(100)
            .orientation(Orientation.SQUARE)
            .creationTS(now)
            .build();

        photo1OfAlbum1.addTaggedPerson(alice, 1);

        Photo photo2OfAlbum1 = Photo.builder()
            .name("Photo 2 of Album 1")
            .fileName("P2A1.jpg")
            .width(100)
            .height(100)
            .orientation(Orientation.SQUARE)
            .creationTS(now)
            .build();

        photo2OfAlbum1.addTaggedPerson(alice, 2);

        Photo photo1OfAlbum2 = Photo.builder()
            .name("Photo 1 of Album 2")
            .fileName("P1A2.jpg")
            .width(100)
            .height(100)
            .orientation(Orientation.SQUARE)
            .creationTS(now)
            .build();

        photo1OfAlbum2.addTaggedPerson(alice, 3);
        photo1OfAlbum2.addTaggedPerson(bob, 4);

        Album album1 = Album.builder()
            .name("Album 1")
            .photos(List.of(
                AlbumPhoto.builder().photo(photo1OfAlbum1).assignmentTS(now).build(),
                AlbumPhoto.builder().photo(photo2OfAlbum1).assignmentTS(now).build()
            ))
            .creationTS(now)
            .build();

        Album album2 = Album.builder()
            .name("Album 2")
            .photos(List.of(AlbumPhoto.builder().photo(photo1OfAlbum2).assignmentTS(now).build()))
            .creationTS(now)
            .build();

        albumRepository.saveAllAndFlush(List.of(album1, album2));

        var result = albumRepository.findAlbumsRankedByDistinctTaggedPersonsWithJooq();

        assertThat(result)
            .containsExactly("Album 2", "Album 1")
            .containsAll(albumRepository.findAlbumsRankedByDistinctTaggedPersonsWithNativeQuery());
    }
}
