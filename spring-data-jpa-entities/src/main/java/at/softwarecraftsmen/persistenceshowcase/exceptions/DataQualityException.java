package at.softwarecraftsmen.persistenceshowcase.exceptions;

public class DataQualityException extends RuntimeException {
    
    private DataQualityException(String msg) {
        this(msg, null);
    }
    
    private DataQualityException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
    
    public static DataQualityException forUnknownColumnValue(Class<?> clazz, Object value) {
        return new DataQualityException("Found unknown value '%s' for type %s in the DB".formatted(value, clazz.getSimpleName()));
    }
}
