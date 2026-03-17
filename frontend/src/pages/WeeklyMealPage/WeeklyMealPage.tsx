import { useWeeklyMeal } from '../../hooks/useMeal';
import { MealCard } from '../../components/MealCard/MealCard';
import type { Meal } from '../../types';
import styles from './WeeklyMealPage.module.css';

const DAY_NAMES = ['월', '화', '수', '목', '금', '토'];
const MEAL_ORDER: Record<string, number> = { '조식': 0, '중식': 1, '석식': 2 };

function toLocalDateStr(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

function getWeekDates(): string[] {
  const today = new Date();
  const day = today.getDay(); // 0=일
  const monday = new Date(today);
  monday.setDate(today.getDate() - (day === 0 ? 6 : day - 1));
  return Array.from({ length: 6 }, (_, i) => {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    return toLocalDateStr(d);
  });
}

function groupByDate(meals: Meal[]): Record<string, Meal[]> {
  return meals.reduce((acc, meal) => {
    if (!acc[meal.mealDate]) acc[meal.mealDate] = [];
    acc[meal.mealDate].push(meal);
    return acc;
  }, {} as Record<string, Meal[]>);
}

export function WeeklyMealPage() {
  const { meals, loading, error } = useWeeklyMeal();
  const weekDates = getWeekDates();
  const today = toLocalDateStr(new Date());
  const grouped = groupByDate(meals);

  return (
    <div className={styles.container}>
      <h2 className={styles.heading}>이번 주 급식</h2>
      {error && <p className={styles.error}>{error}</p>}
      <div className={styles.grid}>
        {weekDates.map((date, idx) => {
          const dayMeals = (grouped[date] ?? []).sort(
            (a, b) => (MEAL_ORDER[a.mealType] ?? 9) - (MEAL_ORDER[b.mealType] ?? 9)
          );
          return (
            <div key={date} className={`${styles.dayCol} ${date === today ? styles.today : ''} ${idx === 5 ? styles.saturday : ''}`}>
              <div className={styles.dayLabel}>{DAY_NAMES[idx]}</div>
              <div className={styles.dayDate}>{date.slice(5).replace('-', '/')}</div>
              {loading ? (
                <div className={styles.skeleton} />
              ) : dayMeals.length ? (
                dayMeals.map((meal) => <MealCard key={meal.id} meal={meal} />)
              ) : (
                <div className={styles.noMeal}>급식 없음</div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
