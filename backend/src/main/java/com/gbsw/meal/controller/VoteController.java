package com.gbsw.meal.controller;

import com.gbsw.meal.dto.request.VoteRequest;
import com.gbsw.meal.dto.response.VoteResultResponse;
import com.gbsw.meal.entity.Vote;
import com.gbsw.meal.security.CustomUserDetails;
import com.gbsw.meal.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<?> vote(@Valid @RequestBody VoteRequest request,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        voteService.vote(request, userDetails.getUserId());
        return ResponseEntity.ok(Map.of("message", "투표 완료"));
    }

    @DeleteMapping("/{mealId}")
    public ResponseEntity<?> cancel(@PathVariable Long mealId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        voteService.cancelVote(mealId, userDetails.getUserId());
        return ResponseEntity.ok(Map.of("message", "투표 취소 완료"));
    }

    @GetMapping("/{mealId}/result")
    public ResponseEntity<VoteResultResponse> result(@PathVariable Long mealId) {
        return ResponseEntity.ok(voteService.getResult(mealId));
    }

    @GetMapping("/{mealId}/mine")
    public ResponseEntity<?> mine(@PathVariable Long mealId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<Vote> vote = voteService.getMyVote(mealId, userDetails.getUserId());
        return ResponseEntity.ok(vote.map(v -> Map.of(
                "voteType", v.getVoteType().name(),
                "dislikeReason", v.getDislikeReason() != null ? v.getDislikeReason().name() : ""
        )).orElse(null));
    }
}
