package com.example.restaurantvoting.repository;

import com.example.restaurantvoting.model.Dish;
import com.example.restaurantvoting.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DishRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private DishRepository dishRepository;

    @Test
    void getAllByRestaurantAndDate_Should_find_dishes_only_with_given_date() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        restaurantRepository.save(restaurant);
        Dish dish1 = new Dish("Test dish 1", 100, restaurant, LocalDate.now());
        Dish dish2 = new Dish("Test dish 2", 100, restaurant, LocalDate.now());
        Dish dish3 = new Dish("Test dish 3", 100, restaurant, LocalDate.now());
        Dish dish4 = new Dish("Test dish 4", 100, restaurant, LocalDate.of(2020, 2, 2));
        dishRepository.saveAll(List.of(dish1, dish2, dish3, dish4));

        List<Dish> result = dishRepository.getAllByRestaurantAndDate(restaurant.getId(), LocalDate.now());

        assertThat(result).containsExactlyInAnyOrder(dish1, dish2, dish3);
    }

    @Test
    void getAllByRestaurantAndDate_Should_return_empty_list_when_restaurant_not_found() {
        Long restaurantId = 6610L;

        List<Dish> result = dishRepository.getAllByRestaurantAndDate(restaurantId, LocalDate.now());

        assertThat(result).isEmpty();
    }

    @Test
    void getByRestaurantAndId_Should_find_dish_when_restaurant_and_dish_present() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        restaurantRepository.save(restaurant);
        Dish expected = new Dish("Test dish", 100, restaurant, LocalDate.now());
        dishRepository.save(expected);

        Dish result = dishRepository.getByRestaurantAndId(restaurant.getId(), expected.getId());

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getByRestaurantAndId_Should_return_null_when_dish_not_found() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        restaurantRepository.save(restaurant);
        Long dishId = 6610L;

        Dish result = dishRepository.getByRestaurantAndId(restaurant.getId(), dishId);

        assertThat(result).isNull();
    }

    @Test
    void removeByRestaurantAndId_Should_remove_dish_when_dish_present() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        restaurantRepository.save(restaurant);
        Dish dish = new Dish("Test dish", 100, restaurant, LocalDate.now());
        dishRepository.save(dish);

        int modificationCount = dishRepository.removeByRestaurantAndId(restaurant.getId(), dish.getId());

        assertThat(modificationCount).isEqualTo(1);
    }

    @Test
    void removeByRestaurantAndId_Should_not_remove_dish_when_dish_not_found() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        restaurantRepository.save(restaurant);
        Long dishId = 6610L;

        int modificationCount = dishRepository.removeByRestaurantAndId(restaurant.getId(), dishId);

        assertThat(modificationCount).isEqualTo(0);
    }
}