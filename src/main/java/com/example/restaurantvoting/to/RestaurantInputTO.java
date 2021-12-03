package com.example.restaurantvoting.to;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantInputTO {

    @NotBlank
    @Size(min = 2, max = 100)
    String name;

    @NotBlank
    @Size(min = 5, max = 100)
    String address;
}