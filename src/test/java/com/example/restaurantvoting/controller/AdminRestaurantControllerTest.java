package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.controller.restaurant.AdminRestaurantController;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.service.RestaurantService;
import com.example.restaurantvoting.to.RestaurantInputTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminRestaurantController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class) },
        excludeAutoConfiguration = { SecurityAutoConfiguration.class})
public class AdminRestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RestaurantService restaurantService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String url = "/api/admin/restaurants";

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

    @Test
    void createRestaurant() throws Exception {
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        r.setId(1L);

        given(restaurantService.create(any(RestaurantInputTO.class))).willReturn(r);

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RestaurantInputTO("Test Restaurant", "Test address"))))
                .andExpect(status().isCreated())
                .andReturn();
        Restaurant result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        String locationHeader = mvcResult.getResponse().getHeader("location");

        assertThat(result).usingRecursiveComparison().isEqualTo(r);
        assertThat(locationHeader).isEqualTo("http://localhost" + url + "/1");
    }

    @Test
    void updateRestaurant() throws Exception {
        Restaurant r = new Restaurant("Test Restaurant", "Test address - updated");

        given(restaurantService.update(anyLong(), any(RestaurantInputTO.class))).willReturn(r);

        String contentAsString = mockMvc.perform(put(url + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RestaurantInputTO("Test Restaurant", "Test address - updated"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Restaurant result = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(result).usingRecursiveComparison().isEqualTo(r);
    }

    @Test
    void deleteRestaurantById() throws Exception {
        doNothing().when(restaurantService).deleteById(anyLong());
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        mockMvc.perform(delete(url + "/1"))
                        .andExpect(status().isNoContent());

        verify(restaurantService).deleteById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1L);
    }
}