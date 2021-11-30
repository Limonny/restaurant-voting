package com.example.restaurantvoting.controller.user;

import com.example.restaurantvoting.model.user.User;
import com.example.restaurantvoting.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@AllArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAll();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getById(userId);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long userId, @RequestParam Boolean enabled) {
        userService.setStatus(userId, enabled);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}