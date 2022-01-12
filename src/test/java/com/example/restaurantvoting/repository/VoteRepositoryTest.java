package com.example.restaurantvoting.repository;

import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.model.Vote;
import com.example.restaurantvoting.model.user.Role;
import com.example.restaurantvoting.model.user.Status;
import com.example.restaurantvoting.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VoteRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoteRepository voteRepository;

    @Test
    void findAllByRestaurantAndDate_Should_count_votes_only_with_given_date() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        restaurantRepository.save(restaurant);
        User user1 = new User("mail1@mail.com", "0000", Role.USER, Status.ACTIVE);
        User user2 = new User("mail2@mail.com", "0000", Role.USER, Status.ACTIVE);
        userRepository.saveAll(List.of(user1, user2));
        Vote vote1 = new Vote(restaurant, user1, LocalDate.now());
        Vote vote2 = new Vote(restaurant, user2, LocalDate.now());
        Vote vote3 = new Vote(restaurant, user2, LocalDate.of(2020, 2, 2));
        voteRepository.saveAll(List.of(vote1, vote2, vote3));

        long votesCount = voteRepository.findAllByRestaurantAndDate(restaurant.getId(), LocalDate.now());

        assertThat(votesCount).isEqualTo(2);
    }

    @Test
    void findAllByRestaurantAndDate_Should_return_zero_when_restaurant_not_found() {
        Long restaurantId = 6610L;

        long votesCount = voteRepository.findAllByRestaurantAndDate(restaurantId, LocalDate.now());

        assertThat(votesCount).isEqualTo(0);
    }

    @Test
    void getByUserAndDate_Should_find_vote_when_user_and_vote_present() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        restaurantRepository.save(restaurant);
        User user = new User("mail1@mail.com", "0000", Role.USER, Status.ACTIVE);
        userRepository.save(user);
        Vote expected = new Vote(restaurant, user, LocalDate.now());
        voteRepository.save(expected);

        Vote result = voteRepository.getByUserAndDate(user.getEmail(), LocalDate.now());

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getByUserAndDate_Should_return_null_when_user_not_found() {
        String email = "";

        Vote result = voteRepository.getByUserAndDate(email, LocalDate.now());

        assertThat(result).isNull();
    }
}