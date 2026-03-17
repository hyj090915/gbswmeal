package com.gbsw.meal.scheduler;

import com.gbsw.meal.service.NeisApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class MealFetchScheduler {

    private final NeisApiService neisApiService;

    @Scheduled(cron = "0 0 7 * * MON-FRI")
    public void fetchTodayMeal() {
        log.info("오늘 급식 자동 수집 시작");
        neisApiService.fetchAndSaveMeal(LocalDate.now());
    }

    @Scheduled(cron = "0 0 18 * * SUN")
    public void fetchWeeklyMeals() {
        log.info("다음주 급식 미리 수집 시작");
        LocalDate nextMonday = LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(1);
        for (int i = 0; i < 5; i++) {
            neisApiService.fetchAndSaveMeal(nextMonday.plusDays(i));
        }
    }
}
