package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.repository.RestaurantRepository;
import com.example.restaurantvoting.to.RestaurantInputTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> getAll() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        if (restaurants.isEmpty()) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "No Restaurants found");
        }

        return restaurants;
    }

    public Restaurant getById(Long restaurantId) {
        return checkIfRestaurantPresentAndGet(restaurantId);
    }

    public Restaurant create(RestaurantInputTO restaurantInputTO) {
        return restaurantRepository.save(new Restaurant(
                restaurantInputTO.getName(),
                restaurantInputTO.getAddress()));
    }

    @Transactional
    public Restaurant update(Long restaurantId, RestaurantInputTO restaurantInputTO) {
        Restaurant restaurant = checkIfRestaurantPresentAndGet(restaurantId);

        restaurant.setName(restaurantInputTO.getName());
        restaurant.setAddress(restaurantInputTO.getAddress());

        return restaurant;
    }

    public void deleteById(Long restaurantId) {
        Integer modificationCount = restaurantRepository.removeById(restaurantId);

        if (modificationCount == 0) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Deletion error. Restaurant with id=" + restaurantId + " not found");
        }
    }

    private Restaurant checkIfRestaurantPresentAndGet(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Restaurant with id=" + restaurantId + " not found");
        }

        return restaurant;
    }
}