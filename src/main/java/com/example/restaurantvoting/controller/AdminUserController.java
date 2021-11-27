package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.model.Status;
import com.example.restaurantvoting.model.User;
import com.example.restaurantvoting.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@AllArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/by")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> setUserStatus(@PathVariable Long userId, @RequestParam Boolean enabled) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        user.setStatus(enabled ? Status.ACTIVE : Status.BANNED);

        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}