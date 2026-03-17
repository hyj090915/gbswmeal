package com.gbsw.meal.service;

import com.gbsw.meal.dto.response.MealResponse;
import com.gbsw.meal.entity.Meal;
import com.gbsw.meal.repository.MealRepository;
import com.gbsw.meal.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final VoteRepository voteRepository;
    private final NeisApiService neisApiService;

    public List<MealResponse> getTodayMeals() {
        LocalDate today = LocalDate.now();
        List<Meal> meals = mealRepository.findByMealDate(today);
        if (meals.isEmpty()) {
            neisApiService.fetchAndSaveMeal(today);
            meals = mealRepository.findByMealDate(today);
        }
        return meals.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MealResponse> getWeeklyMeals() {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate saturday = monday.plusDays(5); // 월~토
        fetchMissingDays(monday, saturday);
        return mealRepository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(monday, saturday)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 전체 급식표: DB에 있는 데이터만 반환 (자동 fetch X - 타임아웃 방지)
    public List<MealResponse> getMonthlyMeals(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return mealRepository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(start, end)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 토요일(조식 있음) 포함, 일요일만 제외
    private void fetchMissingDays(LocalDate start, LocalDate end) {
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (d.getDayOfWeek() == DayOfWeek.SUNDAY) continue;
            if (mealRepository.findByMealDate(d).isEmpty()) {
                neisApiService.fetchAndSaveMeal(d);
            }
        }
    }

    public List<MealResponse> getMealsByDate(LocalDate date) {
        List<Meal> meals = mealRepository.findByMealDate(date);
        if (meals.isEmpty()) {
            neisApiService.fetchAndSaveMeal(date);
            meals = mealRepository.findByMealDate(date);
        }
        return meals.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void fetchMeals(LocalDate date) {
        neisApiService.fetchAndSaveMeal(date);
    }

    private MealResponse toResponse(Meal meal) {
        meal.setLikeCount(voteRepository.countLikesByMealId(meal.getId()));
        meal.setDislikeCount(voteRepository.countDislikesByMealId(meal.getId()));
        return new MealResponse(meal);
    }
}
