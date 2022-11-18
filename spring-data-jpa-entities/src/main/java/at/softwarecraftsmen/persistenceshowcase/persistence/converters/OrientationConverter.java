package at.softwarecraftsmen.persistenceshowcase.persistence.converters;


import at.softwarecraftsmen.persistenceshowcase.entity.Orientation;
import at.softwarecraftsmen.persistenceshowcase.exceptions.DataQualityException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrientationConverter implements AttributeConverter<Orientation, String> {
    @Override
    public String convertToDatabaseColumn(Orientation attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case LANDSCAPE -> "L";
            case PORTRAIT -> "P";
            case SQUARE -> "S";
        };
    }

    @Override
    public Orientation convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "L" -> Orientation.LANDSCAPE;
            case "P" -> Orientation.PORTRAIT;
            case "S" -> Orientation.SQUARE;
            default -> throw DataQualityException.forUnknownColumnValue(Orientation.class, dbData);
        };
    }
}
