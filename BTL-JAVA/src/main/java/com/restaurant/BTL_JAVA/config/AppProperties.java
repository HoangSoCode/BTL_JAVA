package com.restaurant.BTL_JAVA.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Cấu hình tìm kiếm, đọc từ tiền tố {@code app.search.*} trong application.properties.
 */
@ConfigurationProperties(prefix = "app.search")
public record AppProperties(
        @DefaultValue("21.2731") double defaultLat,
        @DefaultValue("106.1946") double defaultLng,
        @DefaultValue("5") double defaultRadiusKm,
        @DefaultValue("50") double maxRadiusKm,
        @DefaultValue("9") int defaultPageSize,
        @DefaultValue("50") int maxPageSize) {
}
