package com.example.restaurantvoting.controller.restaurant;

import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.service.RestaurantService;
import com.example.restaurantvoting.to.RestaurantInputTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurants")
@AllArgsConstructor
public class AdminRestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAll();

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long restaurantId) {
        Restaurant restaurant = restaurantService.getById(restaurantId);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody @Valid RestaurantInputTO restaurantInputTO) {
        Restaurant createdRestaurant = restaurantService.create(restaurantInputTO);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/admin/restaurants" + "/{id}")
                .buildAndExpand(createdRestaurant.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(createdRestaurant);
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long restaurantId,
                                                       @RequestBody @Valid RestaurantInputTO restaurantInputTO) {
        Restaurant updatedRestaurant = restaurantService.update(restaurantId, restaurantInputTO);

        return new ResponseEntity<>(updatedRestaurant, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{restaurantId}")
    public void deleteRestaurantById(@PathVariable Long restaurantId) {
        restaurantService.deleteById(restaurantId);
    }
}