package com.gbsw.meal.controller;

import com.gbsw.meal.dto.response.MealResponse;
import com.gbsw.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @GetMapping("/today")
    public ResponseEntity<List<MealResponse>> today() {
        return ResponseEntity.ok(mealService.getTodayMeals());
    }

    @GetMapping("/week")
    public ResponseEntity<List<MealResponse>> week() {
        return ResponseEntity.ok(mealService.getWeeklyMeals());
    }

    @GetMapping("/{date}")
    public ResponseEntity<List<MealResponse>> byDate(
            @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date) {
        return ResponseEntity.ok(mealService.getMealsByDate(date));
    }

    @GetMapping("/month")
    public ResponseEntity<List<MealResponse>> month(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        LocalDate now = LocalDate.now();
        int y = year == 0 ? now.getYear() : year;
        int m = month == 0 ? now.getMonthValue() : month;
        return ResponseEntity.ok(mealService.getMonthlyMeals(y, m));
    }

    @PostMapping("/fetch")
    public ResponseEntity<?> fetch(@RequestParam(required = false)
                                   @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date) {
        mealService.fetchMeals(date != null ? date : LocalDate.now());
        return ResponseEntity.ok(Map.of("message", "급식 데이터 수집 완료"));
    }
}
