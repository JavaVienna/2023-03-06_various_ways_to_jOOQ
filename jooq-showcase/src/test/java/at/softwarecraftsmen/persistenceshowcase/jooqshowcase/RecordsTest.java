package at.softwarecraftsmen.persistenceshowcase.jooqshowcase;

import at.softwarecraftsmen.jooqshowcase.tables.records.PhotosRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static at.softwarecraftsmen.jooqshowcase.Tables.PHOTOS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgresJooqExtension.class)
class RecordsTest {

    @Test
    void insert_photo_record_and_fetch_count(DSLContext dsl) {
        PhotosRecord record = new PhotosRecord();
        record.setId(1L);
        record.setPhotoName("My First Photo");
        record.setFileName("example.jpg");
        record.setWidth(80);
        record.setHeight(80);
        record.setOrientation("S");
        record.setVersion(1);

        record.attach(dsl.configuration());
        record.insert();

        assertThat(dsl.selectCount().from(PHOTOS).fetchSingle().value1()).isEqualTo(1);
    }
}
