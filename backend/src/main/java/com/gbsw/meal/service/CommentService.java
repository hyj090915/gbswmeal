package com.gbsw.meal.service;

import com.gbsw.meal.dto.request.CommentRequest;
import com.gbsw.meal.dto.response.CommentResponse;
import com.gbsw.meal.entity.Comment;
import com.gbsw.meal.entity.Meal;
import com.gbsw.meal.entity.User;
import com.gbsw.meal.repository.CommentRepository;
import com.gbsw.meal.repository.MealRepository;
import com.gbsw.meal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public List<CommentResponse> getComments(Long mealId) {
        return commentRepository.findByMealIdOrderByCreatedAtDesc(mealId)
                .stream().map(CommentResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse addComment(Long mealId, CommentRequest request, Long userId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("급식을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .meal(meal)
                .user(user)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        return new CommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(request.getContent());
        comment.setRating(request.getRating());
        return new CommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}
