package kr.co.inntavern.dripking.util;

public final class CoordinateUtils {
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    private CoordinateUtils() {
    }

    public static void validateBounds(Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude) {
        validateLatitude(minLatitude, "minLatitude");
        validateLatitude(maxLatitude, "maxLatitude");
        validateLongitude(minLongitude, "minLongitude");
        validateLongitude(maxLongitude, "maxLongitude");

        if (minLatitude > maxLatitude) {
            throw new IllegalArgumentException("minLatitude must be less than or equal to maxLatitude.");
        }

        if (minLongitude > maxLongitude) {
            throw new IllegalArgumentException("minLongitude must be less than or equal to maxLongitude.");
        }
    }

    private static void validateLatitude(Double latitude, String fieldName) {
        if (latitude == null || !Double.isFinite(latitude) || latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException(fieldName + " must be between -90 and 90.");
        }
    }

    private static void validateLongitude(Double longitude, String fieldName) {
        if (longitude == null || !Double.isFinite(longitude) || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException(fieldName + " must be between -180 and 180.");
        }
    }
}
