package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.UserAlreadyExistException;
import com.example.restaurantvoting.model.user.Role;
import com.example.restaurantvoting.model.user.Status;
import com.example.restaurantvoting.model.user.User;
import com.example.restaurantvoting.repository.UserRepository;
import com.example.restaurantvoting.to.UserInputTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "No Users found");
        }

        return users;
    }

    public User getById(Long userId) {
        return checkIfUserPresentAndGet(userId);
    }

    public User create(UserInputTO userInputTO) {
        String email = userInputTO.getEmail().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "User with email: " + email + " already exist");
        }

        return userRepository.save(new User(
                email,
                passwordEncoder.encode(userInputTO.getPassword()),
                Role.USER,
                Status.ACTIVE));
    }

    @Transactional
    public void setStatus(Long userId, Boolean enabled) {
        User user = checkIfUserPresentAndGet(userId);

        user.setStatus(enabled ? Status.ACTIVE : Status.BANNED);
    }

    private User checkIfUserPresentAndGet(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "User with id=" + userId + " not found");
        });
    }
}