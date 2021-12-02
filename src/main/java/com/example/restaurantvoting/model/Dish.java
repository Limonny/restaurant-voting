package com.example.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "dish", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date_entry", "restaurant_id", "name"}, name = "dish_unique_date_restaurant_description_idx")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"restaurant"})
public class Dish extends BaseEntity {

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(max = 200)
    private String name;

    @Column (name = "price", nullable = false)
    @NotNull
    @Min(0)
    private Integer price;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "date_entry", nullable = false)
    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;
}