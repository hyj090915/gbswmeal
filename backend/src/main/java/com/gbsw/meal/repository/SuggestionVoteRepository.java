package com.gbsw.meal.repository;

import com.gbsw.meal.entity.SuggestionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SuggestionVoteRepository extends JpaRepository<SuggestionVote, Long> {
    Optional<SuggestionVote> findBySuggestionIdAndUserId(Long suggestionId, Long userId);
    boolean existsBySuggestionIdAndUserId(Long suggestionId, Long userId);
}
