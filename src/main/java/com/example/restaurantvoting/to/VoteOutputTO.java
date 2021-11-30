package com.example.restaurantvoting.to;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.time.LocalDate;

@Value
public class VoteOutputTO {

    Long id;
    Long restaurantId;
    String user;

    @JsonFormat(pattern = "dd.MM.yyyy")
    LocalDate date;
}