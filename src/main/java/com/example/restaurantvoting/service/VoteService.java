package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.VoteSubmissionException;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.model.user.User;
import com.example.restaurantvoting.model.Vote;
import com.example.restaurantvoting.repository.RestaurantRepository;
import com.example.restaurantvoting.repository.VoteRepository;
import com.example.restaurantvoting.to.VoteOutputTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;

    private final LocalTime votingDeadline = LocalTime.of(11, 0);

    public VoteOutputTO getByDate(User user, LocalDate date) {
        if (date == null) {
            date = getCurrentDate();
        }

        String email = user.getEmail();
        Vote vote = voteRepository.getByUserAndDate(email, date);

        if (vote == null) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "No voting records found for " + date);
        }

        return new VoteOutputTO(
                vote.getId(),
                vote.getRestaurant().getId(),
                email,
                vote.getDate());
    }

    public VoteOutputTO create(User user, Long restaurantId) {
        Restaurant restaurant = checkIfRestaurantPresentAndGet(restaurantId);

        String email = user.getEmail();
        Vote voteFromDB = voteRepository.getByUserAndDate(email, getCurrentDate());

        if (voteFromDB == null) {
            Vote vote = voteRepository.save(new Vote(
                    restaurant,
                    user,
                    getCurrentDate()));

            return new VoteOutputTO(
                    vote.getId(),
                    restaurantId,
                    email,
                    vote.getDate()
            );
        }
        else {
            throw new VoteSubmissionException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Vote for restaurant with id=" + voteFromDB.getRestaurant().getId() +
                            " already been submitted today. Vote can be changed via PUT request prior 11AM MSK");
        }
    }

    @Transactional
    public VoteOutputTO update(User user, Long restaurantId) {
        Restaurant restaurant = checkIfRestaurantPresentAndGet(restaurantId);

        String email = user.getEmail();
        Vote vote = voteRepository.getByUserAndDate(email, getCurrentDate());

        if (vote == null) {
            throw new VoteSubmissionException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "User with email: " + user.getEmail() + " not voted today." +
                            " Vote can be submitted vit POST request");
        }
        else {
            if (LocalTime.now(ZoneId.of("Europe/Moscow")).isBefore(votingDeadline)) {
                vote.setRestaurant(restaurant);
            } else {
                throw new VoteSubmissionException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Vote for restaurant with id=" + vote.getRestaurant().getId() + " already been submitted today." +
                                " Votes cannot be changed past 11AM MSK");
            }
        }

        return new VoteOutputTO(
                vote.getId(),
                restaurantId,
                email,
                vote.getDate()
        );
    }

    public Long getTotalByDate(Long restaurantId, LocalDate date) {
        checkIfRestaurantPresent(restaurantId);

        if (date == null) {
            date = getCurrentDate();
        }

        return voteRepository.findAllByRestaurantAndDate(restaurantId, date);
    }

    private void checkIfRestaurantPresent(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Restaurant with id=" + restaurantId + " not found");
        }
    }

    private Restaurant checkIfRestaurantPresentAndGet(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Restaurant with id=" + restaurantId + " not found");
        }

        return restaurant;
    }

    private LocalDate getCurrentDate() {
        return LocalDate.now(ZoneId.of("Europe/Moscow"));
    }
}