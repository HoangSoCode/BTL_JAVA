package com.restaurant.BTL_JAVA.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.restaurant.BTL_JAVA.dto.PageResponse;
import com.restaurant.BTL_JAVA.dto.RestaurantResponse;
import com.restaurant.BTL_JAVA.dto.RestaurantSearchRequest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"})
class RestaurantServiceTest {

    @Autowired
    private RestaurantService service;

    private RestaurantSearchRequest req(Long min, Long max, Double radius, String sort) {
        return new RestaurantSearchRequest(null, null, min, max, radius, null, null, null, null, sort, 0, 50);
    }

    @Test
    void filterByRadius_returnsOnlyNearbyAndSortedByDistance() {
        PageResponse<RestaurantResponse> res = service.search(req(null, null, 2.0, "distance"));
        assertFalse(res.content().isEmpty());
        double prev = 0;
        for (RestaurantResponse r : res.content()) {
            assertTrue(r.distanceKm() <= 2.0, r.name() + " vượt bán kính");
            assertTrue(r.distanceKm() >= prev);
            prev = r.distanceKm();
        }
    }

    @Test
    void filterByPrice_returnsOverlappingRanges() {
        PageResponse<RestaurantResponse> res = service.search(req(100_000L, 200_000L, null, "price_asc"));
        assertFalse(res.content().isEmpty());
        for (RestaurantResponse r : res.content()) {
            assertTrue(r.priceMax() >= 100_000L && r.priceMin() <= 200_000L);
        }
    }

    @Test
    void combinedPriceAndDistance() {
        PageResponse<RestaurantResponse> res = service.search(req(null, 100_000L, 3.0, null));
        for (RestaurantResponse r : res.content()) {
            assertTrue(r.priceMin() <= 100_000L);
            assertTrue(r.distanceKm() <= 3.0);
        }
    }

    @Test
    void invalidPriceRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.search(req(500_000L, 100_000L, null, null)));
    }

    @Test
    void invalidRadius_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.search(req(null, null, -1.0, null)));
    }
}
