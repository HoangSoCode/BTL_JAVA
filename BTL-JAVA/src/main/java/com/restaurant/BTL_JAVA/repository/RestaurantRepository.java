package com.restaurant.BTL_JAVA.repository;

import com.restaurant.BTL_JAVA.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {

    @Query("select distinct r.cuisine from Restaurant r order by r.cuisine")
    List<String> findDistinctCuisines();
}
