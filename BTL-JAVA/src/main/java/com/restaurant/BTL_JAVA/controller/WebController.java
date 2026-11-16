package com.restaurant.BTL_JAVA.controller;

import com.restaurant.BTL_JAVA.config.AppProperties;
import com.restaurant.BTL_JAVA.dto.RestaurantResponse;
import com.restaurant.BTL_JAVA.exception.ResourceNotFoundException;
import com.restaurant.BTL_JAVA.service.RestaurantService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Controller trả về các trang Thymeleaf. */
@Controller
public class WebController {

    private final RestaurantService service;
    private final AppProperties props;

    public WebController(RestaurantService service, AppProperties props) {
        this.service = service;
        this.props = props;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("defaultLat", props.defaultLat());
        model.addAttribute("defaultLng", props.defaultLng());
        model.addAttribute("defaultRadius", props.defaultRadiusKm());
        model.addAttribute("maxRadius", props.maxRadiusKm());
        return "index";
    }

    @GetMapping("/restaurants/{id}")
    public String detail(@PathVariable Long id, Model model, HttpServletResponse response) {
        try {
            RestaurantResponse restaurant = service.getById(id);
            model.addAttribute("restaurant", restaurant);
            return "restaurant-detail";
        } catch (ResourceNotFoundException ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            model.addAttribute("message", ex.getMessage());
            return "error/404";
        }
    }
}
