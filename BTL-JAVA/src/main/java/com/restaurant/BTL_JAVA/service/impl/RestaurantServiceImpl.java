package com.restaurant.BTL_JAVA.service.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.BTL_JAVA.config.AppProperties;
import com.restaurant.BTL_JAVA.dto.PageResponse;
import com.restaurant.BTL_JAVA.dto.RestaurantResponse;
import com.restaurant.BTL_JAVA.dto.RestaurantSearchRequest;
import com.restaurant.BTL_JAVA.entity.Restaurant;
import com.restaurant.BTL_JAVA.exception.ResourceNotFoundException;
import com.restaurant.BTL_JAVA.repository.RestaurantRepository;
import com.restaurant.BTL_JAVA.repository.RestaurantSpecifications;
import com.restaurant.BTL_JAVA.service.RestaurantService;
import com.restaurant.BTL_JAVA.util.GeoUtils;

@Service
@Transactional(readOnly = true)
public class RestaurantServiceImpl implements RestaurantService {

    private static final List<String> SORT_KEYS = List.of("distance", "price_asc", "price_desc", "rating");

    private final RestaurantRepository repository;
    private final AppProperties props;

    public RestaurantServiceImpl(RestaurantRepository repository, AppProperties props) {
        this.repository = repository;
        this.props = props;
    }

    @Override
    public PageResponse<RestaurantResponse> search(RestaurantSearchRequest req) {
        validate(req);

        final double lat = req.lat() != null ? req.lat() : props.defaultLat();
        final double lng = req.lng() != null ? req.lng() : props.defaultLng();
        final Double radius = req.radiusKm();
        final int page = req.page() != null ? req.page() : 0;
        final int size = req.size() != null ? req.size() : props.defaultPageSize();

        // 1) Lọc thô ở DB: giá, ẩm thực, đánh giá, hộp giới hạn toạ độ
        double[] bbox = radius != null ? GeoUtils.boundingBox(lat, lng, radius) : null;
        List<Restaurant> candidates = repository.findAll(RestaurantSpecifications.matching(
                req.keyword(), req.cuisine(), req.minPrice(), req.maxPrice(),
                req.minRating(), req.maxRating(), bbox));

        // 2) Tính khoảng cách chính xác (Haversine), lọc theo bán kính, sắp xếp
        List<RestaurantResponse> results = candidates.stream()
                .map(r -> RestaurantResponse.from(r,
                        round2(GeoUtils.haversineKm(lat, lng, r.getLatitude(), r.getLongitude()))))
                .filter(r -> radius == null || r.distanceKm() <= radius)
                .sorted(comparator(req.sort()))
                .toList();

        // 3) Phân trang
        int total = results.size();
        int totalPages = (int) Math.ceil(total / (double) size);
        int from = (int) Math.min((long) page * size, total);
        int to = Math.min(from + size, total);
        return new PageResponse<>(results.subList(from, to), page, size, total, totalPages);
    }

    @Override
    public RestaurantResponse getById(Long id) {
        Restaurant r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà hàng với id = " + id));
        return RestaurantResponse.from(r, null);
    }

    @Override
    public List<String> getCuisines() {
        return repository.findDistinctCuisines();
    }

    private Comparator<RestaurantResponse> comparator(String sort) {
        String key = sort == null || sort.isBlank() ? "distance" : sort.toLowerCase();
        Comparator<RestaurantResponse> byDistance = Comparator.comparing(RestaurantResponse::distanceKm);
        return switch (key) {
            case "price_asc" -> Comparator.comparing(RestaurantResponse::priceMin).thenComparing(byDistance);
            case "price_desc" -> Comparator.comparing(RestaurantResponse::priceMax, Comparator.reverseOrder())
                    .thenComparing(byDistance);
            case "rating" -> Comparator.comparing(RestaurantResponse::rating, Comparator.reverseOrder())
                    .thenComparing(byDistance);
            default -> byDistance;
        };
    }

    private void validate(RestaurantSearchRequest r) {
        if (r.minPrice() != null && r.minPrice() < 0) {
            throw new IllegalArgumentException("Giá tối thiểu không được âm");
        }
        if (r.maxPrice() != null && r.maxPrice() < 0) {
            throw new IllegalArgumentException("Giá tối đa không được âm");
        }
        if (r.minPrice() != null && r.maxPrice() != null && r.minPrice() > r.maxPrice()) {
            throw new IllegalArgumentException("Giá tối thiểu không được lớn hơn giá tối đa");
        }
        if (r.radiusKm() != null && (r.radiusKm() <= 0 || r.radiusKm() > props.maxRadiusKm())) {
            throw new IllegalArgumentException("Bán kính phải trong khoảng (0, " + props.maxRadiusKm() + "] km");
        }
        if ((r.lat() == null) != (r.lng() == null)) {
            throw new IllegalArgumentException("Cần cung cấp đồng thời lat và lng");
        }
        if (r.lat() != null && (r.lat() < -90 || r.lat() > 90 || r.lng() < -180 || r.lng() > 180)) {
            throw new IllegalArgumentException("Toạ độ không hợp lệ");
        }
        if (r.minRating() != null && (r.minRating() < 0 || r.minRating() > 5)) {
            throw new IllegalArgumentException("Đánh giá tối thiểu phải từ 0 đến 5");
        }
        if (r.maxRating() != null && (r.maxRating() < 0 || r.maxRating() > 5)) {
            throw new IllegalArgumentException("Đánh giá tối đa phải từ 0 đến 5");
        }
        if (r.page() != null && r.page() < 0) {
            throw new IllegalArgumentException("Số trang không được âm");
        }
        if (r.size() != null && (r.size() < 1 || r.size() > props.maxPageSize())) {
            throw new IllegalArgumentException("Kích thước trang phải từ 1 đến " + props.maxPageSize());
        }
        if (r.sort() != null && !r.sort().isBlank() && !SORT_KEYS.contains(r.sort().toLowerCase())) {
            throw new IllegalArgumentException("Giá trị sort không hợp lệ (distance | price_asc | price_desc | rating)");
        }
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}