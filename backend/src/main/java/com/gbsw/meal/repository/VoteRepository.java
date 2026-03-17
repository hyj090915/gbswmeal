package com.gbsw.meal.repository;

import com.gbsw.meal.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByMealIdAndUserId(Long mealId, Long userId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.meal.id = :mealId AND v.voteType = 'LIKE'")
    int countLikesByMealId(@Param("mealId") Long mealId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.meal.id = :mealId AND v.voteType = 'DISLIKE'")
    int countDislikesByMealId(@Param("mealId") Long mealId);
}
