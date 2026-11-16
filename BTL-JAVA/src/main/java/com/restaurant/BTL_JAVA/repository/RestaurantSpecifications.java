package com.restaurant.BTL_JAVA.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.restaurant.BTL_JAVA.entity.Restaurant;

import jakarta.persistence.criteria.Predicate;

/**
 * Xây dựng điều kiện lọc động. Tham số null nghĩa là "không lọc theo tiêu chí đó".
 */
public final class RestaurantSpecifications {

    private RestaurantSpecifications() {
    }

    public static Specification<Restaurant> matching(String keyword, String cuisine,
                                                     Long minPrice, Long maxPrice,
                                                     Double minRating, double[] bbox) {
        return matching(keyword, cuisine, minPrice, maxPrice, minRating, null, bbox);
    }

    /**
     * @param bbox {minLat, maxLat, minLng, maxLng} hoặc null
     */
    public static Specification<Restaurant> matching(String keyword, String cuisine,
                                                     Long minPrice, Long maxPrice,
                                                     Double minRating, Double maxRating,
                                                     double[] bbox) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.<String>get("name")), like),
                        cb.like(cb.lower(root.<String>get("address")), like),
                        cb.like(cb.lower(root.<String>get("cuisine")), like)));
            }
            if (cuisine != null && !cuisine.isBlank()) {
                predicates.add(cb.equal(root.get("cuisine"), cuisine.trim()));
            }
            // Khoảng giá của quán giao với khoảng giá người dùng chọn
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<Long>get("priceMax"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<Long>get("priceMin"), maxPrice));
            }
            if (minRating != null) {
                if (minRating == 3.5) {
                    predicates.add(cb.greaterThan(root.<BigDecimal>get("rating"), BigDecimal.valueOf(minRating)));
                } else {
                    predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("rating"), BigDecimal.valueOf(minRating)));
                }
            }
            if (maxRating != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("rating"), BigDecimal.valueOf(maxRating)));
            }
            if (bbox != null) {
                predicates.add(cb.between(root.<Double>get("latitude"), bbox[0], bbox[1]));
                predicates.add(cb.between(root.<Double>get("longitude"), bbox[2], bbox[3]));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}