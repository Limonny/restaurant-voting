package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.EntityValidationException;
import com.example.restaurantvoting.model.Dish;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.repository.DishRepository;
import com.example.restaurantvoting.repository.RestaurantRepository;
import com.example.restaurantvoting.to.DishInputTO;
import com.example.restaurantvoting.to.DishOutputTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DishServiceTest {

    @Mock
    private DishRepository dishRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    private DishService dishService;

    @BeforeEach
    void setUp() {
        dishService = new DishService(dishRepository, restaurantRepository);
    }

    @Test
    void getAllByDate_Should_find_all_dishes_and_correctly_transform_them() {
        DishOutputTO expected1 = new DishOutputTO(20L, "Test Dish 1", 100, 1L, LocalDate.now());
        DishOutputTO expected2 = new DishOutputTO(21L, "Test Dish 2", 200, 1L, LocalDate.now());
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        r.setId(1L);
        Dish d1 = new Dish("Test Dish 1", 100, r, LocalDate.now());
        d1.setId(20L);
        Dish d2 = new Dish("Test Dish 2", 200, r, LocalDate.now());
        d2.setId(21L);

        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.getAllByRestaurantAndDate(anyLong(), any(LocalDate.class))).willReturn(List.of(d1, d2));

        List<DishOutputTO> result = dishService.getAllByDate(1L, LocalDate.now());

        assertThat(result).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(expected1, expected2);
    }

    @Test
    void getAllByDate_Should_use_current_date_when_date_not_provided() {
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        r.setId(1L);
        Dish d1 = new Dish("Test Dish 1", 100, r, LocalDate.now());
        d1.setId(20L);
        Dish d2 = new Dish("Test Dish 2", 200, r, LocalDate.now());
        d2.setId(21L);

        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.getAllByRestaurantAndDate(anyLong(), any(LocalDate.class))).willReturn(List.of(d1, d2));
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        dishService.getAllByDate(1L, null);

        verify(dishRepository).getAllByRestaurantAndDate(anyLong(), captor.capture());
        assertThat(captor.getValue()).isEqualTo(LocalDate.now());
    }

    @Test
    void getAllByDate_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.existsById(anyLong())).willReturn(false);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.getAllByDate(1L, LocalDate.now()));
    }

    @Test
    void getAllByDate_Should_throw_EntityNotFoundException_when_dishes_not_present() {
        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.getAllByRestaurantAndDate(anyLong(), any(LocalDate.class))).willReturn(Collections.emptyList());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.getAllByDate(1L, LocalDate.now()));
    }

    @Test
    void getById_Should_find_dish_and_correctly_transform_it() {
        DishOutputTO expected = new DishOutputTO(20L, "Test Dish", 100, 1L, LocalDate.now());
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        r.setId(1L);
        Dish d = new Dish("Test Dish", 100, r, LocalDate.now());
        d.setId(20L);

        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.getByRestaurantAndId(anyLong(), anyLong())).willReturn(d);

        DishOutputTO result = dishService.getById(1L, 20L);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getById_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.existsById(anyLong())).willReturn(false);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.getById(1L, 20L));
    }

    @Test
    void getById_Should_throw_EntityNotFoundException_when_dish_not_found() {
        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.getByRestaurantAndId(anyLong(), anyLong())).willReturn(null);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.getById(1L, 20L));
    }

    @Test
    void create_Should_correctly_transform_input_before_saving() {
        DishOutputTO expected = new DishOutputTO(20L, "Test Dish", 100, 1L, LocalDate.now());
        Restaurant r = new Restaurant("Test Restaurant", "Test Address");
        r.setId(1L);
        Dish d = new Dish("Test Dish", 100, r, LocalDate.now());
        d.setId(20L);
        DishInputTO input = new DishInputTO("Test Dish", 100);

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(r));
        given(dishRepository.save(any(Dish.class))).willReturn(d);

        DishOutputTO result = dishService.create(1L, input);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void create_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.create(1L, new DishInputTO()));
    }

    @Test
    void update_Should_find_dish_and_update_it_then_return_updated_dish() {
        DishOutputTO expected = new DishOutputTO(20L, "Test Dish - updated", 200, 1L, LocalDate.now());
        Restaurant r = new Restaurant("Test Restaurant", "Test Address");
        r.setId(1L);
        Dish d = new Dish("Test Dish", 100, r, LocalDate.now());
        d.setId(20L);
        DishInputTO input = new DishInputTO("Test Dish - updated", 200);

        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.findById(anyLong())).willReturn(Optional.of(d));

        DishOutputTO result = dishService.update(1L, input, 20L);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void update_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.existsById(anyLong())).willReturn(false);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.update(1L, new DishInputTO(), 20L));
    }

    @Test
    void update_Should_throw_EntityNotFoundException_when_dish_not_found() {
        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.update(1L, new DishInputTO(), 20L));
    }

    @Test
    void update_Should_throw_EntityValidationException_when_dish_does_not_belong_to_restaurant() {
        Restaurant r = new Restaurant("Test Restaurant", "Test Address");
        r.setId(6L);
        Dish d = new Dish("Test Dish", 100, r, LocalDate.now());
        d.setId(20L);

        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.findById(anyLong())).willReturn(Optional.of(d));

        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> dishService.update(1L, new DishInputTO(), 20L));
    }

    @Test
    void deleteById_Should_delete_dish_with_given_id() {
        Long restaurantId = 1L;
        Long dishId = 20L;

        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.removeByRestaurantAndId(anyLong(), anyLong())).willReturn(1);
        ArgumentCaptor<Long> firstCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> secondCaptor = ArgumentCaptor.forClass(Long.class);

        dishService.deleteById(restaurantId, dishId);

        verify(dishRepository).removeByRestaurantAndId(firstCaptor.capture(), secondCaptor.capture());
        assertThat(firstCaptor.getValue()).isEqualTo(restaurantId);
        assertThat(secondCaptor.getValue()).isEqualTo(dishId);
    }

    @Test
    void deleteById_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.existsById(anyLong())).willReturn(false);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.deleteById(1L, 20L));
    }

    @Test
    void deleteById_Should_throw_EntityNotFoundException_when_dish_not_found() {
        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(dishRepository.removeByRestaurantAndId(anyLong(), anyLong())).willReturn(0);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> dishService.deleteById(1L, 20L));
    }
}