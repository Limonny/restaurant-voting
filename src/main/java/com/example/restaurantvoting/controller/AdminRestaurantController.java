package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.example.restaurantvoting.util.ValidationUtil.*;

@RestController
@RequestMapping("/api/admin/restaurants")
@AllArgsConstructor
public class AdminRestaurantController {

    private final RestaurantRepository restaurantRepository;

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        if (restaurants.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody @Valid Restaurant restaurant) {
        isEntityNew(restaurant);

        Restaurant createdRestaurant = restaurantRepository.save(restaurant);

        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @PutMapping("{restaurantId}")
    public ResponseEntity<Restaurant> updateRestaurant(@RequestBody @Valid Restaurant restaurant, @PathVariable Long restaurantId) {
        assureIdConsistent(restaurant, restaurantId);

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        return new ResponseEntity<>(updatedRestaurant, HttpStatus.OK);
    }

    @DeleteMapping("{restaurantId}")
    public ResponseEntity<Restaurant> deleteRestaurantById(@PathVariable Long restaurantId) {
        Integer modificationCount = restaurantRepository.delete(restaurantId);

        checkModification(modificationCount, restaurantId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}