package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.IdValidationException;
import com.example.restaurantvoting.model.Dish;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.repository.DishRepository;
import com.example.restaurantvoting.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@Service
@AllArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;

    public Dish save(Long restaurantId, Dish dish) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Restaurant with id=" + restaurantId + " not found",
                    ErrorAttributeOptions.of(MESSAGE));
        }

        Long dishId = dish.getId();
        if (dishId != null) {
            Dish d = dishRepository.getDishByRestaurantAndId(restaurantId, dishId);
            if (d == null) {
                throw new IdValidationException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Dish with id=" + dishId + " doesn't belong to Restaurant with id=" + restaurantId + " or doesn't exist",
                        ErrorAttributeOptions.of(MESSAGE));
            }
        }

        dish.setRestaurant(restaurant);

        return dishRepository.save(dish);
    }
}