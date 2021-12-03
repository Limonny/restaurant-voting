package com.example.restaurantvoting.to;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DishOutputTO {

    Long id;
    String name;
    Integer price;
    Long restaurantId;

    @JsonFormat(pattern = "dd.MM.yyyy")
    LocalDate date;
}