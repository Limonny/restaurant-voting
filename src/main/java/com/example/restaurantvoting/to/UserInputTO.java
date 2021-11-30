package com.example.restaurantvoting.to;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class UserInputTO {

    @Email
    @NotBlank
    @Size(max = 100)
    String email;

    @NotBlank
    @Size(min = 4, max = 32)
    String password;
}