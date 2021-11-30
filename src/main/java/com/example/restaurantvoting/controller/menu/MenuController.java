package com.example.restaurantvoting.controller.menu;

import com.example.restaurantvoting.service.DishService;
import com.example.restaurantvoting.to.DishOutputTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu")
@AllArgsConstructor
public class MenuController {

    private final DishService dishService;

    @GetMapping
    public ResponseEntity<List<DishOutputTO>> getAllDishes(@PathVariable Long restaurantId) {
        List<DishOutputTO> dishes = dishService.getAllByDate(restaurantId, null);

        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @GetMapping("/{dishId}")
    public ResponseEntity<DishOutputTO> getDishById(@PathVariable Long restaurantId, @PathVariable Long dishId) {
        DishOutputTO dishOutputTO = dishService.getById(restaurantId, dishId);

        return new ResponseEntity<>(dishOutputTO, HttpStatus.OK);
    }
}