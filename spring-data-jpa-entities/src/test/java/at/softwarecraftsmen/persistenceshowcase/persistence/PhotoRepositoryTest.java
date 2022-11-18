package at.softwarecraftsmen.persistenceshowcase.persistence;

import at.softwarecraftsmen.persistenceshowcase.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JooqConfiguration.class)
class PhotoRepositoryTest extends AbstractDataJpaTestcontainerTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    void ensureSavingAPhotoWorks() {
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

        Photo photo = Photo.builder()
            .fileName("DSC-4711.jpg")
            .name("Portrait of :)")
            .creationTS(LocalDateTime.now())
            .width(640)
            .height(480)
            .photographer(uk)
            .orientation(Orientation.LANDSCAPE)
            .build();

        var saved = photoRepository.save(photo);

        assertThat(saved).isSameAs(photo);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();
        assertThat(saved.getPhotographer()).isEqualTo(uk);
        assertThat(uk.getId()).isNotNull();
        assertThat(uk.getVersion()).isNotNull();
        assertThat(austria.getId()).isNotNull();
        assertThat(austria.getVersion()).isNotNull();
    }
}
