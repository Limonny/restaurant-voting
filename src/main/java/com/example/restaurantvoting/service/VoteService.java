package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.VoteSubmissionException;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.model.User;
import com.example.restaurantvoting.model.Vote;
import com.example.restaurantvoting.repository.RestaurantRepository;
import com.example.restaurantvoting.repository.UserRepository;
import com.example.restaurantvoting.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    private final LocalTime votingDeadline = LocalTime.of(11, 0);

    public Vote save(Long userId, Long restaurantId) {
        Vote vote = voteRepository.getByUserAndDate(userId, LocalDate.now());
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (restaurant == null) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Restaurant with id=" + restaurantId + " not found",
                    ErrorAttributeOptions.of(MESSAGE));
        }

        if (vote == null) {
            Vote v = new Vote();

            v.setRestaurant(restaurant);
            v.setUser(user);
            v.setDate(LocalDate.now());

            return voteRepository.save(v);
        }
        else {
            if (LocalTime.now(ZoneId.of("Europe/Moscow")).isBefore(votingDeadline)) {
                vote.setRestaurant(restaurant);

                voteRepository.save(vote);
            }
            else {
                throw new VoteSubmissionException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Vote for restaurant with id=" + vote.getRestaurant().getId() + " already been submitted today." +
                                " Votes cannot be changed past 11AM MSK",
                        ErrorAttributeOptions.of(MESSAGE));
            }
        }

        return vote;
    }
}