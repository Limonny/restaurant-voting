package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.UserAlreadyExistException;
import com.example.restaurantvoting.model.Role;
import com.example.restaurantvoting.model.Status;
import com.example.restaurantvoting.model.User;
import com.example.restaurantvoting.repository.UserRepository;
import com.example.restaurantvoting.to.UserTO;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(UserTO userTO) {
        String email = userTO.getEmail().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "User with email: " + email + " already exist",
                    ErrorAttributeOptions.of(MESSAGE)
            );
        }

        return userRepository.save(new User(
                email,
                passwordEncoder.encode(userTO.getPassword()),
                Role.USER,
                Status.ACTIVE));
    }
}