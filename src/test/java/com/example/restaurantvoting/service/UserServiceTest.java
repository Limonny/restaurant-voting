package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.EntityValidationException;
import com.example.restaurantvoting.exception.UserAlreadyExistException;
import com.example.restaurantvoting.model.user.Role;
import com.example.restaurantvoting.model.user.Status;
import com.example.restaurantvoting.model.user.User;
import com.example.restaurantvoting.repository.UserRepository;
import com.example.restaurantvoting.to.UserInputTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void getAll_Should_return_all_found_users() {
        User u1 = new User("mail1@mail.com", "0000", Role.USER, Status.ACTIVE);
        User u2 = new User("mail2@mail.com", "0000", Role.USER, Status.ACTIVE);

        given(userRepository.findAll()).willReturn(List.of(u1, u2));

        List<User> result = userService.getAll();

        assertThat(result).containsExactlyInAnyOrder(u1, u2);
    }

    @Test
    void getAll_Should_throw_EntityNotFoundException_when_result_list_is_empty() {
        given(userRepository.findAll()).willReturn(Collections.emptyList());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> userService.getAll());
    }

    @Test
    void getById_Should_return_found_user() {
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(u));

        User result = userService.getById(1L);

        assertThat(result).isEqualTo(u);
    }

    @Test
    void getById_Should_throw_EntityNotFoundException_when_user_not_found() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> userService.getById(1L));
    }

    @Test
    void create_Should_correctly_transform_input_before_saving() {
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        UserInputTO input = new UserInputTO("mail@mail.com", "0000");

        given(userRepository.save(any(User.class))).willReturn(u);

        User result = userService.create(input);

        assertThat(result).isEqualTo(u);
    }

    @Test
    void create_Should_transform_email_to_lower_case() {
        String email = "mail@mail.com";
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        UserInputTO input = new UserInputTO("MAil@mail.COM", "0000");

        given(userRepository.save(any(User.class))).willReturn(u);

        User result = userService.create(input);

        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void create_Should_throw_UserAlreadyExistException_when_user_already_present() {
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        UserInputTO input = new UserInputTO("mail@mail.com", "0000");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(u));

        assertThatExceptionOfType(UserAlreadyExistException.class)
                .isThrownBy(() -> userService.create(input));
    }

    @Test
    void setStatus_Should_update_user_status() {
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(u));

        userService.setStatus(1L, false);

        assertThat(u.getStatus() == Status.BANNED).isTrue();
    }

    @Test
    void setStatus_Should_not_update_user_status_when_role_is_admin() {
        User u = new User("mail@mail.com", "0000", Role.ADMIN, Status.ACTIVE);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(u));

        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> userService.setStatus(1L, false));
    }
}