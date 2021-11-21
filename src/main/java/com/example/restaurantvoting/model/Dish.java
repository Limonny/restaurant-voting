package com.example.restaurantvoting.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "dish", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date_entry", "restaurant_id", "description"}, name = "dish_unique_date_restaurant_description_idx")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"restaurant"})
public class Dish extends BaseEntity {

    @Column(name = "description", nullable = false)
    @NotBlank
    @Max(200)
    private String description;

    @Column (name = "price", nullable = false)
    @NotNull
    @Min(0)
    private Integer price;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "date_entry", nullable = false)
    @NotNull
    private LocalDate date;
}