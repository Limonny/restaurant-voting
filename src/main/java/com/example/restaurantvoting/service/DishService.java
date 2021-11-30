package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.EntityValidationException;
import com.example.restaurantvoting.model.Dish;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.repository.DishRepository;
import com.example.restaurantvoting.repository.RestaurantRepository;
import com.example.restaurantvoting.to.DishInputTO;
import com.example.restaurantvoting.to.DishOutputTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;

    public List<DishOutputTO> getAllByDate(Long restaurantId, LocalDate date) {
        checkIfRestaurantPresent(restaurantId);

        if (date == null) {
            date = getCurrentDate();
        }

        List<Dish> dishes = dishRepository.getAllByRestaurantAndDate(restaurantId, date);

        if (dishes.isEmpty()) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    String.format("No Dishes present for Restaurant with id:%d for %s", restaurantId, date));
        }

        return dishes.stream()
                .map(dish -> new DishOutputTO(
                        dish.getId(),
                        dish.getName(),
                        dish.getPrice(),
                        dish.getRestaurant().getId(),
                        dish.getDate()))
                .collect(Collectors.toList());
    }

    public DishOutputTO getById(Long restaurantId, Long dishId) {
        checkIfRestaurantPresent(restaurantId);

        Dish dish = dishRepository.getByRestaurantAndId(restaurantId, dishId);

        if (dish == null) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Dish with id:" + dishId + " not found");
        }

        return new DishOutputTO(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                dish.getRestaurant().getId(),
                dish.getDate());
    }

    public DishOutputTO create(Long restaurantId, DishInputTO dishInputTO) {
        Restaurant restaurant = checkIfRestaurantPresentAndGet(restaurantId);

        Dish dish = dishRepository.save(new Dish(
                dishInputTO.getName(),
                dishInputTO.getPrice(),
                restaurant,
                getCurrentDate()));

        return new DishOutputTO(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                restaurantId,
                dish.getDate());
    }

    @Transactional
    public DishOutputTO update(Long restaurantId, DishInputTO dishInputTO, Long dishId) {
        checkIfRestaurantPresent(restaurantId);

        Dish dish = dishRepository.findById(dishId).orElse(null);

        if (dish == null) {
            throw new EntityValidationException(
                    HttpStatus.NOT_FOUND,
                    "Dish with id=" + dishId + " not found");
        }
        else {
            if (!dish.getRestaurant().getId().equals(restaurantId)) {
                throw new EntityValidationException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Dish with id=" + dishId + " doesn't belong to Restaurant with id=" + restaurantId);
            }
        }

        dish.setName(dishInputTO.getName());
        dish.setPrice(dishInputTO.getPrice());

        return new DishOutputTO(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                restaurantId,
                dish.getDate()
        );
    }

    public void deleteById(Long restaurantId, Long dishId) {
        checkIfRestaurantPresent(restaurantId);

        Integer modificationCount = dishRepository.removeByRestaurantAndId(restaurantId, dishId);

        if (modificationCount == 0) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Deletion error. Dish with id=" + dishId + " not found");
        }
    }

    private void checkIfRestaurantPresent(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Restaurant with id=" + restaurantId + " not found");
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

    private LocalDate getCurrentDate() {
        return LocalDate.now(ZoneId.of("Europe/Moscow"));
    }
}