package com.restaurant.BTL_JAVA.dto;

import java.math.BigDecimal;

import com.restaurant.BTL_JAVA.entity.Restaurant;

public record RestaurantResponse(
        Long id,
        String name,
        String cuisine,
        String address,
        String description,
        String phone,
        String openingHours,
        Long priceMin,
        Long priceMax,
        BigDecimal rating,
        Double latitude,
        Double longitude,
        Double distanceKm,
        String imageUrl) {

    public static RestaurantResponse from(Restaurant r, Double distanceKm) {
        return new RestaurantResponse(r.getId(), r.getName(), r.getCuisine(), r.getAddress(),
                r.getDescription(), r.getPhone(), r.getOpeningHours(), r.getPriceMin(), r.getPriceMax(),
                r.getRating(), r.getLatitude(), r.getLongitude(), distanceKm, r.getImageUrl());
    }
}