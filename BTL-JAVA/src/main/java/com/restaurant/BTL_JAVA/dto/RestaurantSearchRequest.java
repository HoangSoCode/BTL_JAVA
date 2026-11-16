package com.restaurant.BTL_JAVA.dto;

public record RestaurantSearchRequest(
        String keyword,
        String cuisine,
        Long minPrice,
        Long maxPrice,
        Double radiusKm,
        Double lat,
        Double lng,
        Double minRating,
        Double maxRating,
        String sort,
        Integer page,
        Integer size) {
}