package com.example.restaurantvoting.vote;

import com.example.restaurantvoting.AbstractTest;
import com.example.restaurantvoting.to.VoteOutputTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = "user@mail.com")
class VoteControllerTest extends AbstractTest {

    private final String restUrl = "/api/vote";
    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getVoteByDate() throws Exception {
        VoteOutputTO expected = new VoteOutputTO(
                1L, 2L, "user@mail.com", LocalDate.of(2021, 1, 1));

        ResultActions resultActions = perform(MockMvcRequestBuilders.get(restUrl+ "?date=01.01.2021"))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        VoteOutputTO actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void createVote() throws Exception {
        VoteOutputTO expected = new VoteOutputTO(
                3L, 1L, "user@mail.com", LocalDate.now());

        ResultActions resultActions = perform(MockMvcRequestBuilders.post(restUrl+ "?restaurantId=1"))
                .andExpect(status().isCreated());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        VoteOutputTO actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void updateVote() throws Exception {
        VoteOutputTO expected = new VoteOutputTO(
                3L, 2L, "user@mail.com", LocalDate.now());

        perform(MockMvcRequestBuilders.post(restUrl+ "?restaurantId=1"))
                .andExpect(status().isCreated());

        if (LocalTime.now(ZoneId.of("Europe/Moscow")).isBefore(LocalTime.of(11, 0))) {
            ResultActions resultActions = perform(MockMvcRequestBuilders.put(restUrl+ "?restaurantId=2"))
                    .andExpect(status().isOk());

            String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
            VoteOutputTO actual = mapper.readValue(contentAsString, new TypeReference<>(){});

            assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
        }
        else {
            perform(MockMvcRequestBuilders.put(restUrl+ "?restaurantId=2"))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    void getTotalVotesForRestaurantByDate() throws Exception {
        ResultActions resultActions = perform(MockMvcRequestBuilders.get(restUrl + "/total/1?date=01.01.2021"))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Long actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).isEqualTo(1L);
    }
}