package com.restaurant.BTL_JAVA.util;

/**
 * Các hàm tiện ích địa lý: khoảng cách Haversine và hộp giới hạn (bounding box).
 */
public final class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0088;
    private static final double KM_PER_DEGREE_LAT = 111.32;

    private GeoUtils() {
    }

    /** Khoảng cách đường chim bay (km) giữa hai toạ độ. */
    public static double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.min(1.0, Math.sqrt(a)));
    }

    /**
     * Hộp giới hạn quanh một điểm, dùng để lọc thô ở tầng DB trước khi tính Haversine chính xác.
     *
     * @return mảng {minLat, maxLat, minLng, maxLng}
     */
    public static double[] boundingBox(double lat, double lng, double radiusKm) {
        double latDelta = radiusKm / KM_PER_DEGREE_LAT;
        double cos = Math.max(0.01, Math.cos(Math.toRadians(lat)));
        double lngDelta = radiusKm / (KM_PER_DEGREE_LAT * cos);
        return new double[]{lat - latDelta, lat + latDelta, lng - lngDelta, lng + lngDelta};
    }

    /** Dịch một điểm theo km về hướng Bắc / Đông (dùng cho dữ liệu mẫu). */
    public static double[] offsetKm(double lat, double lng, double northKm, double eastKm) {
        double newLat = lat + northKm / KM_PER_DEGREE_LAT;
        double cos = Math.max(0.01, Math.cos(Math.toRadians(lat)));
        double newLng = lng + eastKm / (KM_PER_DEGREE_LAT * cos);
        return new double[]{newLat, newLng};
    }
}
