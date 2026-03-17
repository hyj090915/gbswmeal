package com.gbsw.meal.repository;

import com.gbsw.meal.entity.MenuSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuggestionRepository extends JpaRepository<MenuSuggestion, Long> {
    List<MenuSuggestion> findAllByOrderByVoteCountDesc();
}
