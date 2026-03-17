package com.gbsw.meal.dto.response;

import com.gbsw.meal.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private Long id;
    private Long mealId;
    private String userEmail;
    private String content;
    private int rating;
    private LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.mealId = comment.getMeal().getId();
        this.userEmail = comment.getUser().getEmail();
        this.content = comment.getContent();
        this.rating = comment.getRating();
        this.createdAt = comment.getCreatedAt();
    }
}
