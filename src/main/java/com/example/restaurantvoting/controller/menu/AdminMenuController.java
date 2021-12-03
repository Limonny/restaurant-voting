package com.example.restaurantvoting.controller.menu;

import com.example.restaurantvoting.service.DishService;
import com.example.restaurantvoting.to.DishInputTO;
import com.example.restaurantvoting.to.DishOutputTO;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurants/{restaurantId}/menu")
@AllArgsConstructor
public class AdminMenuController {

    private final DishService dishService;

    @GetMapping
    public ResponseEntity<List<DishOutputTO>> getAllDishesByDate(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        List<DishOutputTO> dishes = dishService.getAllByDate(restaurantId, date);

        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @GetMapping("/{dishId}")
    public ResponseEntity<DishOutputTO> getDishById(@PathVariable Long restaurantId, @PathVariable Long dishId) {
        DishOutputTO dishOutputTO = dishService.getById(restaurantId, dishId);

        return new ResponseEntity<>(dishOutputTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DishOutputTO> createDish(@PathVariable Long restaurantId,
                                                   @RequestBody @Valid DishInputTO dishInputTO) {
        DishOutputTO dishOutputTO = dishService.create(restaurantId, dishInputTO);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/admin/restaurants/{restaurantId}/menu" + "/{id}")
                .buildAndExpand(restaurantId, dishOutputTO.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(dishOutputTO);
    }

    @PutMapping("/{dishId}")
    public ResponseEntity<DishOutputTO> updateDish(
            @PathVariable Long restaurantId,
            @RequestBody @Valid DishInputTO dishInputTO,
            @PathVariable Long dishId) {
        DishOutputTO dishOutputTO = dishService.update(restaurantId, dishInputTO, dishId);

        return new ResponseEntity<>(dishOutputTO, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{dishId}")
    public void deleteDishById(@PathVariable Long restaurantId, @PathVariable Long dishId) {
        dishService.deleteById(restaurantId, dishId);
    }
}