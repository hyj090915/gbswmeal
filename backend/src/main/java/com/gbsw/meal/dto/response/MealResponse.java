package com.gbsw.meal.dto.response;

import com.gbsw.meal.entity.Meal;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MealResponse {
    private Long id;
    private LocalDate mealDate;
    private String mealType;
    private String dishNames;
    private String calInfo;
    private String ntrInfo;
    private int likeCount;
    private int dislikeCount;

    public MealResponse(Meal meal) {
        this.id = meal.getId();
        this.mealDate = meal.getMealDate();
        this.mealType = meal.getMealType();
        this.dishNames = meal.getDishNames();
        this.calInfo = meal.getCalInfo();
        this.ntrInfo = meal.getNtrInfo();
        this.likeCount = meal.getLikeCount();
        this.dislikeCount = meal.getDislikeCount();
    }
}
