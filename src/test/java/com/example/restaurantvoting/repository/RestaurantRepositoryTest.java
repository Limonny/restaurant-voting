package com.example.restaurantvoting.repository;

import com.example.restaurantvoting.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void removeById_Should_remove_restaurant_when_restaurant_present() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        restaurantRepository.save(restaurant);

        int modificationCount = restaurantRepository.removeById(restaurant.getId());

        assertThat(modificationCount).isEqualTo(1);
    }

    @Test
    void removeById_Should_not_remove_restaurant_when_restaurant_not_found() {
        Long restaurantId = 6610L;

        int modificationCount = restaurantRepository.removeById(restaurantId);

        assertThat(modificationCount).isEqualTo(0);
    }
}