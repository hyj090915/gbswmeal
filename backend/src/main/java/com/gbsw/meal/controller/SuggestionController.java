package com.gbsw.meal.controller;

import com.gbsw.meal.dto.request.SuggestionRequest;
import com.gbsw.meal.dto.response.SuggestionResponse;
import com.gbsw.meal.security.CustomUserDetails;
import com.gbsw.meal.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    @GetMapping
    public ResponseEntity<List<SuggestionResponse>> list(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(suggestionService.getSuggestions(userId));
    }

    @PostMapping
    public ResponseEntity<SuggestionResponse> create(@Valid @RequestBody SuggestionRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(suggestionService.createSuggestion(request, userDetails.getUserId()));
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> vote(@PathVariable Long id,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        suggestionService.vote(id, userDetails.getUserId());
        return ResponseEntity.ok(Map.of("message", "공감 완료"));
    }

    @DeleteMapping("/{id}/vote")
    public ResponseEntity<?> cancelVote(@PathVariable Long id,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        suggestionService.cancelVote(id, userDetails.getUserId());
        return ResponseEntity.ok(Map.of("message", "공감 취소 완료"));
    }
}
