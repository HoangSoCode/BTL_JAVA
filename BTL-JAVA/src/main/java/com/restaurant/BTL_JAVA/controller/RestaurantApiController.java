package com.restaurant.BTL_JAVA.controller;

import com.restaurant.BTL_JAVA.dto.PageResponse;
import com.restaurant.BTL_JAVA.dto.RestaurantResponse;
import com.restaurant.BTL_JAVA.dto.RestaurantSearchRequest;
import com.restaurant.BTL_JAVA.service.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API.
 * <pre>
 * GET /api/restaurants/search?lat=..&amp;lng=..&amp;radiusKm=3&amp;minPrice=50000&amp;maxPrice=200000&amp;sort=distance
 * GET /api/restaurants/{id}
 * GET /api/restaurants/cuisines
 * </pre>
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantApiController {

    private final RestaurantService service;

    public RestaurantApiController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public PageResponse<RestaurantResponse> search(RestaurantSearchRequest request) {
        return service.search(request);
    }

    @GetMapping("/cuisines")
    public List<String> cuisines() {
        return service.getCuisines();
    }

    @GetMapping("/{id}")
    public RestaurantResponse detail(@PathVariable Long id) {
        return service.getById(id);
    }
}
