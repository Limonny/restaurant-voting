package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.controller.menu.MenuController;
import com.example.restaurantvoting.service.DishService;
import com.example.restaurantvoting.to.DishOutputTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MenuController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class) },
        excludeAutoConfiguration = { SecurityAutoConfiguration.class})
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DishService dishService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String url = "/api/restaurants/1/menu";

    {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllDishes() throws Exception {
        DishOutputTO d1 = new DishOutputTO(20L, "Test Dish 1", 100, 1L, LocalDate.now());
        DishOutputTO d2 = new DishOutputTO(21L, "Test Dish 2", 200, 1L, LocalDate.now());

        given(dishService.getAllByDate(anyLong(), nullable(LocalDate.class))).willReturn(List.of(d1, d2));

        String contentAsString = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<DishOutputTO> result = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(result).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(d1, d2);
    }

    @Test
    void getDishById() throws Exception {
        DishOutputTO d = new DishOutputTO(20L, "Test Dish", 100, 1L, LocalDate.now());

        given(dishService.getById(anyLong(), anyLong())).willReturn(d);

        String contentAsString = mockMvc.perform(get(url + "/20"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DishOutputTO result = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(result).usingRecursiveComparison().isEqualTo(d);
    }
}