package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.model.Dish;
import com.example.restaurantvoting.repository.DishRepository;
import com.example.restaurantvoting.service.DishService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static com.example.restaurantvoting.util.ValidationUtil.*;

@RestController
@RequestMapping("/api/admin/restaurants/{restaurantId}/menu")
@AllArgsConstructor
public class AdminMenuController {

    private final DishRepository dishRepository;
    private final DishService dishService;

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
        Dish dish = dishRepository.getDishByRestaurantAndId(restaurantId, dishId);

        if (dish == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dish, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Dish> createDish(@PathVariable Long restaurantId, @RequestBody @Valid Dish dish) {
        isEntityNew(dish);

        Dish createdDish = dishService.save(restaurantId, dish);

        return new ResponseEntity<>(createdDish, HttpStatus.CREATED);
    }

    @PutMapping("/{dishId}")
    public ResponseEntity<Dish> updateDish(
            @PathVariable Long restaurantId,
            @RequestBody @Valid Dish dish,
            @PathVariable Long dishId) {
        assureIdConsistent(dish, dishId);

        Dish updatedDish = dishService.save(restaurantId, dish);

        return new ResponseEntity<>(updatedDish, HttpStatus.OK);
    }

    @DeleteMapping("/{dishId}")
    public ResponseEntity<Dish> deleteDishById(@PathVariable Long restaurantId, @PathVariable Long dishId) {
        Integer modificationCount = dishRepository.delete(restaurantId, dishId);

        checkModification(modificationCount, dishId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}