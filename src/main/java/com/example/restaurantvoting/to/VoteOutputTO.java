package com.example.restaurantvoting.to;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VoteOutputTO {

    Long id;
    Long restaurantId;
    String user;

    @JsonFormat(pattern = "dd.MM.yyyy")
    LocalDate date;
}