package com.gbsw.meal.dto.response;

import com.gbsw.meal.entity.MenuSuggestion;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class SuggestionResponse {
    private Long id;
    private String title;
    private String description;
    private String userEmail;
    private int voteCount;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private boolean myVote;

    public SuggestionResponse(MenuSuggestion suggestion, boolean myVote) {
        this.id = suggestion.getId();
        this.title = suggestion.getTitle();
        this.description = suggestion.getDescription();
        this.userEmail = suggestion.getUser().getEmail();
        this.voteCount = suggestion.getVoteCount();
        this.deadline = suggestion.getDeadline();
        this.createdAt = suggestion.getCreatedAt();
        this.myVote = myVote;
    }
}
