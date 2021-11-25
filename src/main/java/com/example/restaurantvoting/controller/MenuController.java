package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.model.Dish;
import com.example.restaurantvoting.repository.DishRepository;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu")
@AllArgsConstructor
public class MenuController {

    private final DishRepository dishRepository;

    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishesByDate(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        List<Dish> dishes = dishRepository.getAllByRestaurantAndDate(restaurantId, date == null ? LocalDate.now() : date);

        if (dishes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @GetMapping("/{dishId}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long restaurantId, @PathVariable Long dishId) {
        Dish dish = dishRepository.getByRestaurantAndId(restaurantId, dishId);

        if (dish == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dish, HttpStatus.OK);
    }
}