package at.softwarecraftsmen.persistenceshowcase.persistence.converters;

import at.softwarecraftsmen.persistenceshowcase.entity.Orientation;
import at.softwarecraftsmen.persistenceshowcase.exceptions.DataQualityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OrientationConverterTest {

    private OrientationConverter converter;

    @BeforeEach
    void setup() {
        converter = new OrientationConverter();
    }

    @Test
    void ensureProperNullHandlingInConversion() {
        // expect
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void ensureProperDetectionOfUnknownValuesInDBDuringConversion() {
        // expect
        var dqEx = Assertions.assertThrows(DataQualityException.class, () -> converter.convertToEntityAttribute("X"));
        assertThat(dqEx).hasMessageContaining("Found unknown value 'X'")
                        .hasMessageEndingWith("for type Orientation in the DB");
    }

    @ParameterizedTest
    @MethodSource
    void ensureProperMappingDuringConversion(Orientation orientation, String dbValue) {
        // expect
        assertThat(converter.convertToDatabaseColumn(orientation)).isEqualTo(dbValue);
        assertThat(converter.convertToEntityAttribute(dbValue)).isEqualTo(orientation);
    }

    static Stream<Arguments> ensureProperMappingDuringConversion() {
        return Stream.of(
            Arguments.of(Orientation.LANDSCAPE, "L"),
            Arguments.of(Orientation.PORTRAIT, "P"),
            Arguments.of(Orientation.SQUARE, "S")
        );
    }
}