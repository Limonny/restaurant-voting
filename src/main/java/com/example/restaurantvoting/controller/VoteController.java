package com.example.restaurantvoting.controller;

import com.example.restaurantvoting.model.Vote;
import com.example.restaurantvoting.repository.VoteRepository;
import com.example.restaurantvoting.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/vote")
@AllArgsConstructor
public class VoteController {

    private final VoteRepository voteRepository;
    private final VoteService voteService;

    @GetMapping
    public ResponseEntity<Vote> getVoteByDate(@RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Vote vote = voteRepository.getByUserAndDate(1L, date == null ? LocalDate.now() : date);

        if (vote == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(vote, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Vote> vote(@RequestParam Long restaurantId) {
        Vote vote = voteService.save(1L, restaurantId);

        return new ResponseEntity<>(vote, HttpStatus.OK);
    }
}