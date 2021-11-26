package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.model.Vote;
import com.example.restaurantvoting.repository.VoteRepository;
import com.example.restaurantvoting.security.SecurityUser;
import com.example.restaurantvoting.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/vote")
@AllArgsConstructor
public class VoteController {

    private final VoteRepository voteRepository;
    private final VoteService voteService;

    @GetMapping
    public ResponseEntity<Vote> getVoteByDate(@AuthenticationPrincipal SecurityUser securityUser,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Vote vote = voteRepository.getByUserAndDate(securityUser.getUsername(), date == null ? LocalDate.now() : date);

        if (vote == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(vote, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Vote> createVote(@AuthenticationPrincipal SecurityUser securityUser,
                                     @RequestParam Long restaurantId) {
        Vote vote = voteService.create(securityUser.getUser(), restaurantId);

        return new ResponseEntity<>(vote, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Vote> updateVote(@AuthenticationPrincipal SecurityUser securityUser,
                                           @RequestParam Long restaurantId) {
        Vote vote = voteService.update(securityUser.getUser(), restaurantId);

        return new ResponseEntity<>(vote, HttpStatus.OK);
    }
}