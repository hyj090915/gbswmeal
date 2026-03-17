package com.gbsw.meal.service;

import com.gbsw.meal.dto.request.SuggestionRequest;
import com.gbsw.meal.dto.response.SuggestionResponse;
import com.gbsw.meal.entity.MenuSuggestion;
import com.gbsw.meal.entity.SuggestionVote;
import com.gbsw.meal.entity.User;
import com.gbsw.meal.repository.SuggestionRepository;
import com.gbsw.meal.repository.SuggestionVoteRepository;
import com.gbsw.meal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final SuggestionVoteRepository suggestionVoteRepository;
    private final UserRepository userRepository;

    public List<SuggestionResponse> getSuggestions(Long userId) {
        return suggestionRepository.findAllByOrderByVoteCountDesc()
                .stream()
                .map(s -> new SuggestionResponse(s,
                        userId != null && suggestionVoteRepository.existsBySuggestionIdAndUserId(s.getId(), userId)))
                .collect(Collectors.toList());
    }

    @Transactional
    public SuggestionResponse createSuggestion(SuggestionRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        MenuSuggestion suggestion = MenuSuggestion.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .deadline(request.getDeadline())
                .voteCount(0)
                .build();

        return new SuggestionResponse(suggestionRepository.save(suggestion), false);
    }

    @Transactional
    public void vote(Long suggestionId, Long userId) {
        MenuSuggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("제안을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (suggestionVoteRepository.existsBySuggestionIdAndUserId(suggestionId, userId)) {
            throw new IllegalStateException("이미 공감한 제안입니다.");
        }

        suggestionVoteRepository.save(SuggestionVote.builder()
                .suggestion(suggestion)
                .user(user)
                .build());

        suggestion.setVoteCount(suggestion.getVoteCount() + 1);
        suggestionRepository.save(suggestion);
    }

    @Transactional
    public void cancelVote(Long suggestionId, Long userId) {
        SuggestionVote vote = suggestionVoteRepository
                .findBySuggestionIdAndUserId(suggestionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("공감 내역이 없습니다."));

        MenuSuggestion suggestion = vote.getSuggestion();
        suggestionVoteRepository.delete(vote);
        suggestion.setVoteCount(Math.max(0, suggestion.getVoteCount() - 1));
        suggestionRepository.save(suggestion);
    }
}
