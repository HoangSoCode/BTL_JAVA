package com.restaurant.BTL_JAVA.service;

import com.restaurant.BTL_JAVA.dto.PageResponse;
import com.restaurant.BTL_JAVA.dto.RestaurantResponse;
import com.restaurant.BTL_JAVA.dto.RestaurantSearchRequest;

import java.util.List;

public interface RestaurantService {

    /** Tìm nhà hàng theo giá, khoảng cách và các tiêu chí khác. */
    PageResponse<RestaurantResponse> search(RestaurantSearchRequest request);

    RestaurantResponse getById(Long id);

    List<String> getCuisines();
}
