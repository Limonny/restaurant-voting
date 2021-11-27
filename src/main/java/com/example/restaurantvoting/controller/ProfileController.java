package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.model.User;
import com.example.restaurantvoting.security.SecurityUser;
import com.example.restaurantvoting.service.UserService;
import com.example.restaurantvoting.to.UserTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<User> getProfileInfo(@AuthenticationPrincipal SecurityUser securityUser) {
        return new ResponseEntity<>(securityUser.getUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> register(@RequestBody @Valid UserTO userTO) {
        User registeredUser = userService.create(userTO);

        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
}