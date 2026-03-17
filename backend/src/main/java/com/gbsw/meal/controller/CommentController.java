package com.gbsw.meal.controller;

import com.gbsw.meal.dto.request.CommentRequest;
import com.gbsw.meal.dto.response.CommentResponse;
import com.gbsw.meal.security.CustomUserDetails;
import com.gbsw.meal.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{mealId}")
    public ResponseEntity<List<CommentResponse>> list(@PathVariable Long mealId) {
        return ResponseEntity.ok(commentService.getComments(mealId));
    }

    @PostMapping("/{mealId}")
    public ResponseEntity<CommentResponse> create(@PathVariable Long mealId,
                                                   @Valid @RequestBody CommentRequest request,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(commentService.addComment(mealId, request, userDetails.getUserId()));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(@PathVariable Long commentId,
                                                   @Valid @RequestBody CommentRequest request,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request, userDetails.getUserId()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(@PathVariable Long commentId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUserId());
        return ResponseEntity.ok(Map.of("message", "댓글 삭제 완료"));
    }
}
