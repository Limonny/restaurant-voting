package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.repository.RestaurantRepository;
import com.example.restaurantvoting.to.RestaurantInputTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        restaurantService = new RestaurantService(restaurantRepository);
    }

    @Test
    void getAll_Should_return_all_found_restaurants() {
        Restaurant r1 = new Restaurant("Test Restaurant 1", "Test address 1");
        Restaurant r2 = new Restaurant("Test Restaurant 2", "Test address 2");

        given(restaurantRepository.findAll()).willReturn(List.of(r1, r2));

        List<Restaurant> result = restaurantService.getAll();

        assertThat(result).containsExactlyInAnyOrder(r1, r2);
    }

    @Test
    void getAll_Should_throw_EntityNotFoundException_when_result_list_is_empty() {
        given(restaurantRepository.findAll()).willReturn(Collections.emptyList());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> restaurantService.getAll());
    }

    @Test
    void getById_Should_return_found_restaurant() {
        Restaurant r = new Restaurant("Test Restaurant", "Test address");

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(r));

        Restaurant result = restaurantService.getById(anyLong());

        assertThat(result).isEqualTo(r);
    }

    @Test
    void getById_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> restaurantService.getById(anyLong()));
    }

    @Test
    void create_Should_save_restaurant_and_return_saved_restaurant() {
        RestaurantInputTO input = new RestaurantInputTO("Test Restaurant", "Test address");
        Restaurant r = new Restaurant("Test Restaurant", "Test address");

        given(restaurantRepository.save(any(Restaurant.class))).willReturn(r);

        Restaurant result = restaurantService.create(input);

        verify(restaurantRepository).save(any(Restaurant.class));
        assertThat(result).isEqualTo(r);
    }

    @Test
    void create_Should_correctly_transform_input_before_saving() {
        String name = "Test Restaurant";
        String address = "Test Address";
        RestaurantInputTO input = new RestaurantInputTO(name, address);

        given(restaurantRepository.save(any(Restaurant.class))).willReturn(null);
        ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);

        restaurantService.create(input);

        verify(restaurantRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(name);
        assertThat(captor.getValue().getAddress()).isEqualTo(address);
    }

    @Test
    void update_Should_find_restaurant_and_update_it_then_return_updated_restaurant() {
        String name = "Test Restaurant - updated";
        String address = "Test Address - updated";
        RestaurantInputTO input = new RestaurantInputTO(name, address);
        Restaurant r = new Restaurant("Test Restaurant", "Test address");

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(r));

        restaurantService.update(anyLong(), input);

        assertThat(r.getName()).isEqualTo(name);
        assertThat(r.getAddress()).isEqualTo(address);
    }

    @Test
    void update_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> restaurantService.update(anyLong(), new RestaurantInputTO()));
    }

    @Test
    void deleteById_Should_delete_restaurant_with_given_id() {
        Long restaurantId = 12L;

        given(restaurantRepository.removeById(anyLong())).willReturn(1);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        restaurantService.deleteById(restaurantId);

        verify(restaurantRepository).removeById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(restaurantId);
    }

    @Test
    void deleteById_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.removeById(anyLong())).willReturn(0);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> restaurantService.deleteById(anyLong()));
    }
}