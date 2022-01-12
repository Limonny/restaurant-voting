package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.controller.menu.AdminMenuController;
import com.example.restaurantvoting.service.DishService;
import com.example.restaurantvoting.to.DishInputTO;
import com.example.restaurantvoting.to.DishOutputTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminMenuController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class) },
        excludeAutoConfiguration = { SecurityAutoConfiguration.class})
public class AdminMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DishService dishService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String url = "/api/admin/restaurants/1/menu";

    {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllDishes() throws Exception {
        DishOutputTO d1 = new DishOutputTO(20L, "Test Dish 1", 100, 1L, LocalDate.of(2020, 2, 2));
        DishOutputTO d2 = new DishOutputTO(21L, "Test Dish 2", 200, 1L, LocalDate.of(2020, 2, 2));

        given(dishService.getAllByDate(anyLong(), nullable(LocalDate.class))).willReturn(List.of(d1, d2));
        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);

        String contentAsString = mockMvc.perform(get(url + "?date=02.02.2020"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<DishOutputTO> result = mapper.readValue(contentAsString, new TypeReference<>(){});

        verify(dishService).getAllByDate(longCaptor.capture(), dateCaptor.capture());
        assertThat(longCaptor.getValue()).isEqualTo(1L);
        assertThat(dateCaptor.getValue()).isEqualTo(LocalDate.of(2020, 2, 2));
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

    @Test
    void createDish() throws Exception {
        DishOutputTO d = new DishOutputTO(20L, "Test Dish", 100, 1L, LocalDate.now());

        given(dishService.create(anyLong(), any(DishInputTO.class))).willReturn(d);

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new DishInputTO("Test Dish", 100))))
                .andExpect(status().isCreated())
                .andReturn();
        DishOutputTO result = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        String locationHeader = mvcResult.getResponse().getHeader("location");

        assertThat(result).usingRecursiveComparison().isEqualTo(d);
        assertThat(locationHeader).isEqualTo("http://localhost" + url + "/20");
    }

    @Test
    void updateDish() throws Exception {
        DishOutputTO d = new DishOutputTO(20L, "Test Dish - updated", 100, 1L, LocalDate.now());

        given(dishService.update(anyLong(), any(DishInputTO.class), anyLong())).willReturn(d);

        String contentAsString = mockMvc.perform(put(url + "/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new DishInputTO("Test Dish - updated", 100))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DishOutputTO result = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(result).usingRecursiveComparison().isEqualTo(d);
    }

    @Test
    void deleteDishById() throws Exception {
        doNothing().when(dishService).deleteById(anyLong(), anyLong());
        ArgumentCaptor<Long> firstCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> secondCaptor = ArgumentCaptor.forClass(Long.class);

        mockMvc.perform(delete(url + "/20"))
                .andExpect(status().isNoContent());

        verify(dishService).deleteById(firstCaptor.capture(), secondCaptor.capture());
        assertThat(firstCaptor.getValue()).isEqualTo(1L);
        assertThat(secondCaptor.getValue()).isEqualTo(20L);
    }
}