package com.example.restaurantvoting.to;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.time.LocalDate;

@Value
public class DishOutputTO {

    Long id;
    String name;
    Integer price;
    Long restaurantId;

    @JsonFormat(pattern = "dd.MM.yyyy")
    LocalDate date;
}