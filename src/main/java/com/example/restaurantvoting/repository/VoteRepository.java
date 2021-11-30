package com.example.restaurantvoting.repository;

import com.example.restaurantvoting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
@Transactional(readOnly = true)
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.restaurant.id=:restaurantId AND v.date=:date")
    Long findAllByRestaurantAndDate(Long restaurantId, LocalDate date);

    @Query("SELECT v FROM Vote v WHERE v.user.email=:email AND v.date=:date")
    Vote getByUserAndDate(String email, LocalDate date);
}