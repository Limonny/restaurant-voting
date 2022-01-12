package com.example.restaurantvoting.service;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.VoteSubmissionException;
import com.example.restaurantvoting.model.Restaurant;
import com.example.restaurantvoting.model.Vote;
import com.example.restaurantvoting.model.user.Role;
import com.example.restaurantvoting.model.user.Status;
import com.example.restaurantvoting.model.user.User;
import com.example.restaurantvoting.repository.RestaurantRepository;
import com.example.restaurantvoting.repository.VoteRepository;
import com.example.restaurantvoting.to.VoteOutputTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        voteService = new VoteService(voteRepository, restaurantRepository);
    }

    @Test
    void getByDate_Should_find_vote_and_correctly_transform_it() {
        VoteOutputTO expected = new VoteOutputTO(10L, 1L, "mail@mail.com", LocalDate.now());
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        r.setId(1L);
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        Vote v = new Vote(r, u, LocalDate.now());
        v.setId(10L);

        given(voteRepository.getByUserAndDate(anyString(), any(LocalDate.class))).willReturn(v);

        VoteOutputTO result = voteService.getByDate(u, LocalDate.now());

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getByDate_Should_use_current_date_when_date_not_provided() {
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        r.setId(1L);
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        Vote v = new Vote(r, u, LocalDate.now());
        v.setId(10L);

        given(voteRepository.getByUserAndDate(anyString(), any(LocalDate.class))).willReturn(v);
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        voteService.getByDate(u, null);

        verify(voteRepository).getByUserAndDate(anyString(), captor.capture());
        assertThat(captor.getValue()).isEqualTo(LocalDate.now());
    }

    @Test
    void getByDate_Should_throw_EntityNotFoundException_when_vote_not_found() {
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);

        given(voteRepository.getByUserAndDate(anyString(), any(LocalDate.class))).willReturn(null);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> voteService.getByDate(u, LocalDate.now()));
    }

    @Test
    void create_Should_save_new_vote_when_vote_was_not_submitted_yet_today() {
        VoteOutputTO expected = new VoteOutputTO(10L, 1L, "mail@mail.com", LocalDate.now());
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        Vote v = new Vote(r, u, LocalDate.now());
        v.setId(10L);

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(r));
        given(voteRepository.getByUserAndDate(anyString(), any(LocalDate.class))).willReturn(null);
        given(voteRepository.save(any(Vote.class))).willReturn(v);

        VoteOutputTO result = voteService.create(u, 1L);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void create_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> voteService.create(new User(), 1L));
    }

    @Test
    void create_Should_throw_VoteSubmissionException_when_vote_already_been_submitted_today() {
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        Vote v = new Vote(r, u, LocalDate.now());

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(r));
        given(voteRepository.getByUserAndDate(anyString(), any(LocalDate.class))).willReturn(v);

        assertThatExceptionOfType(VoteSubmissionException.class)
                .isThrownBy(() -> voteService.create(u, 1L));
    }

    @Test
    void update_Should_find_vote_and_update_it_when_vote_already_been_submitted_today_and_before_voting_deadline() {
        VoteOutputTO expected = new VoteOutputTO(10L, 1L, "mail@mail.com", LocalDate.now());
        Restaurant r = new Restaurant("Test Restaurant - new Vote", "Test address - new Vote");
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        Vote v = new Vote(new Restaurant(), u, LocalDate.now());
        v.setId(10L);

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(r));
        given(voteRepository.getByUserAndDate(anyString(), any(LocalDate.class))).willReturn(v);

        LocalTime mockedTime = LocalTime.of(10, 0);
        try (MockedStatic<LocalTime> mockedStatic = mockStatic(LocalTime.class)) {
            mockedStatic.when(() -> LocalTime.now(any(ZoneId.class))).thenReturn(mockedTime);

            VoteOutputTO result = voteService.update(u, 1L);

            assertThat(v.getRestaurant()).isEqualTo(r);
            assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        }
    }

    @Test
    void update_Should_throw_VoteSubmissionException_when_vote_already_been_submitted_today_and_past_voting_deadline() {
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        r.setId(1L);
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);
        Vote v = new Vote(r, u, LocalDate.now());

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(r));
        given(voteRepository.getByUserAndDate(anyString(), any(LocalDate.class))).willReturn(v);

        LocalTime mockedTime = LocalTime.of(12, 0);
        try (MockedStatic<LocalTime> mockedStatic = mockStatic(LocalTime.class)) {
            mockedStatic.when(() -> LocalTime.now(any(ZoneId.class))).thenReturn(mockedTime);

            assertThatExceptionOfType(VoteSubmissionException.class)
                    .isThrownBy(() -> voteService.update(u, 1L));
        }
    }

    @Test
    void update_Should_throw_VoteSubmissionException_when_vote_was_note_submitted_today() {
        Restaurant r = new Restaurant("Test Restaurant", "Test address");
        User u = new User("mail@mail.com", "0000", Role.USER, Status.ACTIVE);

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(r));
        given(voteRepository.getByUserAndDate(anyString(), any(LocalDate.class))).willReturn(null);

        assertThatExceptionOfType(VoteSubmissionException.class)
                .isThrownBy(() -> voteService.update(u, 1L));
    }

    @Test
    void update_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> voteService.update(new User(), 1L));
    }

    @Test
    void getTotalByDate_Should_return_total_votes_count() {
        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(voteRepository.findAllByRestaurantAndDate(anyLong(), any(LocalDate.class))).willReturn(15L);

        Long result = voteService.getTotalByDate(1L, LocalDate.now());

        assertThat(result).isEqualTo(15L);
    }

    @Test
    void getTotalByDate_Should_use_current_date_when_date_not_provided() {
        given(restaurantRepository.existsById(anyLong())).willReturn(true);
        given(voteRepository.findAllByRestaurantAndDate(anyLong(), any(LocalDate.class))).willReturn(15L);
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        voteService.getTotalByDate(1L, null);

        verify(voteRepository).findAllByRestaurantAndDate(anyLong(), captor.capture());
        assertThat(captor.getValue()).isEqualTo(LocalDate.now());
    }

    @Test
    void getTotalByDate_Should_throw_EntityNotFoundException_when_restaurant_not_found() {
        given(restaurantRepository.existsById(anyLong())).willReturn(false);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> voteService.getTotalByDate(1L, LocalDate.now()));
    }
}