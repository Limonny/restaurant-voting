package com.example.restaurantvoting.controller.user;

import com.example.restaurantvoting.model.user.User;
import com.example.restaurantvoting.security.SecurityUser;
import com.example.restaurantvoting.service.UserService;
import com.example.restaurantvoting.to.UserInputTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

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
    public ResponseEntity<User> register(@RequestBody @Valid UserInputTO userInputTO) {
        User registeredUser = userService.create(userInputTO);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/profile").build().toUri();

        return ResponseEntity.created(uriOfNewResource).body(registeredUser);
    }
}