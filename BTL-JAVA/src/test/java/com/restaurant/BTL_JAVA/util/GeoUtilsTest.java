package com.restaurant.BTL_JAVA.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoUtilsTest {

    @Test
    void haversine_samePoint_isZero() {
        assertEquals(0.0, GeoUtils.haversineKm(21.0, 105.0, 21.0, 105.0), 1e-9);
    }

    @Test
    void haversine_hanoiToHcmc_isAbout1140Km() {
        double d = GeoUtils.haversineKm(21.0285, 105.8542, 10.8231, 106.6297);
        assertEquals(1139, d, 15);
    }

    @Test
    void offsetKm_roundTripMatchesHaversine() {
        double[] p = GeoUtils.offsetKm(21.2731, 106.1946, 3.0, 4.0);
        double d = GeoUtils.haversineKm(21.2731, 106.1946, p[0], p[1]);
        assertEquals(5.0, d, 0.1);
    }

    @Test
    void boundingBox_containsPointsWithinRadius() {
        double[] box = GeoUtils.boundingBox(21.2731, 106.1946, 5);
        double[] p = GeoUtils.offsetKm(21.2731, 106.1946, 3.0, 4.0);
        assertTrue(p[0] >= box[0] && p[0] <= box[1]);
        assertTrue(p[1] >= box[2] && p[1] <= box[3]);
    }
}
