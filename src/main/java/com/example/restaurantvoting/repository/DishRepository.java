package com.example.restaurantvoting.repository;

import com.example.restaurantvoting.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Long> {

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=:restaurantId AND d.date=:date")
    List<Dish> getAllByRestaurantAndDate(Long restaurantId, LocalDate date);

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=:restaurantId AND d.id=:dishId")
    Dish getDishByRestaurantAndId(Long restaurantId, Long dishId);

    @Modifying
    @Query("DELETE FROM Dish d WHERE d.restaurant.id=:restaurantId AND d.id=:dishId")
    Integer delete(Long restaurantId, Long dishId);
}