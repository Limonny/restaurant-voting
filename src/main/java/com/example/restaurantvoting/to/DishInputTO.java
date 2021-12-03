package com.example.restaurantvoting.to;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DishInputTO {

    @NotBlank
    @Size(max = 200)
    String name;

    @NotNull
    @Min(0)
    Integer price;
}