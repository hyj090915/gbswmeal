package com.gbsw.meal.dto.request;

import com.gbsw.meal.entity.Vote.DislikeReason;
import com.gbsw.meal.entity.Vote.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VoteRequest {

    @NotNull
    private Long mealId;

    @NotNull
    private VoteType voteType;

    private DislikeReason dislikeReason;
}
