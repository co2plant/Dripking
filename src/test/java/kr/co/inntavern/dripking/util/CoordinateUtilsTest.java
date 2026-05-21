package kr.co.inntavern.dripking.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoordinateUtilsTest {
    @Test
    void validateBoundsAllowsValidCoordinateRange() {
        assertDoesNotThrow(() -> CoordinateUtils.validateBounds(34.0, 35.0, 135.0, 136.0));
    }

    @Test
    void validateBoundsRejectsLatitudeOutsideWorldRange() {
        assertThrows(IllegalArgumentException.class,
                () -> CoordinateUtils.validateBounds(-91.0, 35.0, 135.0, 136.0));
    }

    @Test
    void validateBoundsRejectsLongitudeOutsideWorldRange() {
        assertThrows(IllegalArgumentException.class,
                () -> CoordinateUtils.validateBounds(34.0, 35.0, 135.0, 181.0));
    }

    @Test
    void validateBoundsRejectsReversedLatitudeRange() {
        assertThrows(IllegalArgumentException.class,
                () -> CoordinateUtils.validateBounds(35.0, 34.0, 135.0, 136.0));
    }

    @Test
    void validateBoundsRejectsReversedLongitudeRange() {
        assertThrows(IllegalArgumentException.class,
                () -> CoordinateUtils.validateBounds(34.0, 35.0, 136.0, 135.0));
    }

    @Test
    void validateBoundsRejectsNonFiniteCoordinates() {
        assertThrows(IllegalArgumentException.class,
                () -> CoordinateUtils.validateBounds(Double.NaN, 35.0, 135.0, 136.0));
    }
}
