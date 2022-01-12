package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.controller.restaurant.RestaurantController;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.service.RestaurantService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestaurantController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class) },
        excludeAutoConfiguration = { SecurityAutoConfiguration.class})
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RestaurantService restaurantService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String url = "/api/restaurants";

    @Test
    void getAllRestaurants() throws Exception {
        Restaurant r1 = new Restaurant("Test Restaurant 1", "Test address 1");
        Restaurant r2 = new Restaurant("Test Restaurant 2", "Test address 2");

        given(restaurantService.getAll()).willReturn(List.of(r1, r2));

        String contentAsString = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Restaurant> result = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(result).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(r1, r2);
    }

    @Test
    void getRestaurantById() throws Exception {
        Restaurant r = new Restaurant("Test Restaurant", "Test address");

        given(restaurantService.getById(anyLong())).willReturn(r);

        String contentAsString = mockMvc.perform(get(url + "/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Restaurant result = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(result).usingRecursiveComparison().isEqualTo(r);
    }
}