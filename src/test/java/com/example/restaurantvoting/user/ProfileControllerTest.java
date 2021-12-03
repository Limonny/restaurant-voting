package com.example.restaurantvoting.user;

import com.example.restaurantvoting.AbstractTest;
import com.example.restaurantvoting.model.user.Role;
import com.example.restaurantvoting.model.user.Status;
import com.example.restaurantvoting.model.user.User;
import com.example.restaurantvoting.to.UserInputTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTest extends AbstractTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithAnonymousUser
    void register() throws Exception {
        User expected = new User("newuser@mail.com", null, Role.USER, Status.ACTIVE);
        expected.setId(4L);

        ResultActions resultActions = perform(MockMvcRequestBuilders.post("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new UserInputTO("newuser@mail.com", "pass"))))
                .andExpect(status().isCreated());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        User actual = mapper.readValue(contentAsString, new TypeReference<>(){});

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}