package at.softwarecraftsmen.persistenceshowcase.entity;

import java.util.Objects;

public enum Orientation {
    LANDSCAPE,
    PORTRAIT,
    SQUARE
    ;

    public static Orientation of(Integer width, Integer height) {
        if (width == height) return SQUARE;
        else if (width < height) return PORTRAIT;
        else return LANDSCAPE;
    }
}
