package com.gbsw.meal.service;

import com.gbsw.meal.dto.request.VoteRequest;
import com.gbsw.meal.dto.response.VoteResultResponse;
import com.gbsw.meal.entity.Meal;
import com.gbsw.meal.entity.User;
import com.gbsw.meal.entity.Vote;
import com.gbsw.meal.repository.MealRepository;
import com.gbsw.meal.repository.UserRepository;
import com.gbsw.meal.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    @Transactional
    public void vote(VoteRequest request, Long userId) {
        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new IllegalArgumentException("급식을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Optional<Vote> existing = voteRepository.findByMealIdAndUserId(meal.getId(), userId);

        if (existing.isPresent()) {
            Vote vote = existing.get();
            if (vote.getVoteType() == request.getVoteType()) {
                voteRepository.delete(vote);
            } else {
                vote.setVoteType(request.getVoteType());
                vote.setDislikeReason(request.getDislikeReason());
                voteRepository.save(vote);
            }
        } else {
            voteRepository.save(Vote.builder()
                    .meal(meal)
                    .user(user)
                    .voteType(request.getVoteType())
                    .dislikeReason(request.getDislikeReason())
                    .build());
        }
    }

    @Transactional
    public void cancelVote(Long mealId, Long userId) {
        voteRepository.findByMealIdAndUserId(mealId, userId)
                .ifPresent(voteRepository::delete);
    }

    public VoteResultResponse getResult(Long mealId) {
        return new VoteResultResponse(
                mealId,
                voteRepository.countLikesByMealId(mealId),
                voteRepository.countDislikesByMealId(mealId)
        );
    }

    public Optional<Vote> getMyVote(Long mealId, Long userId) {
        return voteRepository.findByMealIdAndUserId(mealId, userId);
    }
}
