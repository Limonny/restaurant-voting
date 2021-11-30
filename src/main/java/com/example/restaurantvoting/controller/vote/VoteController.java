package com.example.restaurantvoting.controller.vote;

import com.example.restaurantvoting.security.SecurityUser;
import com.example.restaurantvoting.service.VoteService;
import com.example.restaurantvoting.to.VoteOutputTO;
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

    private final VoteService voteService;

    @GetMapping
    public ResponseEntity<VoteOutputTO> getVoteByDate(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        VoteOutputTO voteOutputTO = voteService.getByDate(securityUser.getUser(), date);

        return new ResponseEntity<>(voteOutputTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<VoteOutputTO> createVote(@AuthenticationPrincipal SecurityUser securityUser,
                                                   @RequestParam Long restaurantId) {
        VoteOutputTO voteOutputTO = voteService.create(securityUser.getUser(), restaurantId);

        return new ResponseEntity<>(voteOutputTO, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<VoteOutputTO> updateVote(@AuthenticationPrincipal SecurityUser securityUser,
                                                   @RequestParam Long restaurantId) {
        VoteOutputTO voteOutputTO = voteService.update(securityUser.getUser(), restaurantId);

        return new ResponseEntity<>(voteOutputTO, HttpStatus.OK);
    }

    @GetMapping("/total/{restaurantId}")
    public ResponseEntity<Long> getTotalVotesForRestaurantByDate(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Long voteCount = voteService.getTotalByDate(restaurantId, date);

        return new ResponseEntity<>(voteCount, HttpStatus.OK);
    }
}