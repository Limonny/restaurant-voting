package com.example.restaurantvoting.restaurant;

import com.example.restaurantvoting.AbstractTest;
import com.example.restaurantvoting.model.Restaurant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = "admin@mail.com")
class AdminRestaurantControllerTest extends AbstractTest {

    private final String REST_URL = "/api/admin/restaurants";
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAllRestaurants() throws Exception {
        List<Restaurant> expected = List.of(
                new Restaurant("McDonald's", "1201 Broadway, Nashville, TN 37203, United States"),
                new Restaurant("Burger King", "1501 Charlotte Ave, Nashville, TN 37203, United States"));
        expected.get(0).setId(1L);
        expected.get(1).setId(2L);

        ResultActions resultActions = perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        List<Restaurant> actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void getRestaurantById() throws Exception {
        Restaurant expected = new Restaurant("McDonald's", "1201 Broadway, Nashville, TN 37203, United States");
        expected.setId(1L);

        ResultActions resultActions = perform(MockMvcRequestBuilders.get(REST_URL + "/1"))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Restaurant actual = mapper.readValue(contentAsString, Restaurant.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createRestaurant() throws Exception {
        Restaurant expected = new Restaurant("New Restaurant", "Some address");
        expected.setId(3L);

        ResultActions resultActions = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Restaurant("New Restaurant", "Some address"))))
                .andExpect(status().isCreated());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Restaurant actual = mapper.readValue(contentAsString, Restaurant.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void updateRestaurant() throws Exception {
        Restaurant expected = new Restaurant("Changed Name", "1201 Broadway, Nashville, TN 37203, United States");
        expected.setId(1L);

        ResultActions resultActions = perform(MockMvcRequestBuilders.put(REST_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(
                        new Restaurant("Changed Name", "1201 Broadway, Nashville, TN 37203, United States"))))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Restaurant actual = mapper.readValue(contentAsString, Restaurant.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void deleteRestaurantById() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/1"))
                .andExpect(status().isNoContent());

        perform(MockMvcRequestBuilders.delete(REST_URL + "/1"))
                .andExpect(status().isNotFound());
    }
}