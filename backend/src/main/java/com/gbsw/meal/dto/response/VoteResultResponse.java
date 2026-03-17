package com.gbsw.meal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteResultResponse {
    private Long mealId;
    private int likeCount;
    private int dislikeCount;
}
