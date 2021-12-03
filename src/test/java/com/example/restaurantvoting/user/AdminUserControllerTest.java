package com.example.restaurantvoting.user;

import com.example.restaurantvoting.AbstractTest;
import com.example.restaurantvoting.model.user.Role;
import com.example.restaurantvoting.model.user.Status;
import com.example.restaurantvoting.model.user.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = "admin@mail.com")
class AdminUserControllerTest extends AbstractTest {

    private final String restUrl = "/api/admin/users";
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAllUsers() throws Exception {
        List<User> expected = List.of(
                new User("user@mail.com", null, Role.USER, Status.ACTIVE),
                new User("admin@mail.com", null, Role.ADMIN, Status.ACTIVE),
                new User("unlucky1user@mail.com", null, Role.USER, Status.BANNED)
        );
        expected.get(0).setId(1L);
        expected.get(1).setId(2L);
        expected.get(2).setId(3L);

        ResultActions resultActions = perform(MockMvcRequestBuilders.get(restUrl))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        List<User> actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getUserById() throws Exception {
        User expected = new User("unlucky1user@mail.com", null, Role.USER, Status.BANNED);
        expected.setId(3L);

        ResultActions resultActions = perform(MockMvcRequestBuilders.get(restUrl + "/3"))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        User actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void updateUserStatus() throws Exception {
        User expected = new User("unlucky1user@mail.com", null, Role.USER, Status.ACTIVE);
        expected.setId(3L);

        perform(MockMvcRequestBuilders.patch(restUrl + "/3?enabled=true"))
                .andExpect(status().isNoContent());

        ResultActions resultActions = perform(MockMvcRequestBuilders.get(restUrl + "/3"))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        User actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}