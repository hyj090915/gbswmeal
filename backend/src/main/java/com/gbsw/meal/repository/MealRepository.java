package com.gbsw.meal.repository;

import com.gbsw.meal.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByMealDate(LocalDate mealDate);
    List<Meal> findByMealDateBetweenOrderByMealDateAscMealTypeAsc(LocalDate start, LocalDate end);
    Optional<Meal> findByMealDateAndMealType(LocalDate mealDate, String mealType);
}
