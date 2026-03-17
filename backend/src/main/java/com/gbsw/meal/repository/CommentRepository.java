package com.gbsw.meal.repository;

import com.gbsw.meal.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMealIdOrderByCreatedAtDesc(Long mealId);
}
