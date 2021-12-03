package com.example.restaurantvoting.menu;

import com.example.restaurantvoting.AbstractTest;
import com.example.restaurantvoting.to.DishOutputTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = "user@mail.com")
class MenuControllerTest extends AbstractTest {

    private final String restUrl = "/api/restaurants/1/menu";
    private final LocalDate today = LocalDate.now();
    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllDishesByDate() throws Exception {
        List<DishOutputTO> expected = List.of(
                new DishOutputTO(1L, "Big Mac", 10, 1L, today),
                new DishOutputTO(2L, "McDouble", 10, 1L, today),
                new DishOutputTO(3L, "Quarter Pounder", 10, 1L, today),
                new DishOutputTO(4L, "Crispy Chicken Sandwich", 10, 1L, today),
                new DishOutputTO(5L, "World Famous Fries", 10, 1L, today)
        );

        ResultActions resultActions = perform(MockMvcRequestBuilders.get(restUrl))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        List<DishOutputTO> actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void getDishById() throws Exception {
        DishOutputTO expected = new DishOutputTO(1L, "Big Mac", 10, 1L, today);

        ResultActions resultActions = perform(MockMvcRequestBuilders.get(restUrl + "/1"))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        DishOutputTO actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}