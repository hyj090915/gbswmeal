import { useState } from 'react';
import { useMonthlyMeal } from '../../hooks/useMeal';
import type { Meal } from '../../types';
import styles from './SchedulePage.module.css';

const MEAL_ORDER: Record<string, number> = { '조식': 0, '중식': 1, '석식': 2 };
const WEEK_LABELS = ['일', '월', '화', '수', '목', '금', '토'];

function groupByDate(meals: Meal[]): Record<string, Meal[]> {
  return meals.reduce((acc, meal) => {
    if (!acc[meal.mealDate]) acc[meal.mealDate] = [];
    acc[meal.mealDate].push(meal);
    return acc;
  }, {} as Record<string, Meal[]>);
}

function getDaysInMonth(year: number, month: number): (string | null)[] {
  const firstDay = new Date(year, month - 1, 1).getDay();
  const daysCount = new Date(year, month, 0).getDate();
  const cells: (string | null)[] = Array(firstDay).fill(null);
  for (let d = 1; d <= daysCount; d++) {
    cells.push(`${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`);
  }
  return cells;
}

export function SchedulePage() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [selected, setSelected] = useState<string | null>(null);

  const { meals, loading } = useMonthlyMeal(year, month);
  const grouped = groupByDate(meals);
  const cells = getDaysInMonth(year, month);
  const today = now.toISOString().slice(0, 10);

  const prevMonth = () => {
    setSelected(null);
    if (month === 1) { setYear(y => y - 1); setMonth(12); }
    else setMonth(m => m - 1);
  };

  const nextMonth = () => {
    setSelected(null);
    if (month === 12) { setYear(y => y + 1); setMonth(1); }
    else setMonth(m => m + 1);
  };

  const sortedSelected = selected
    ? [...(grouped[selected] ?? [])].sort((a, b) => (MEAL_ORDER[a.mealType] ?? 9) - (MEAL_ORDER[b.mealType] ?? 9))
    : [];

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <button className={styles.navBtn} onClick={prevMonth}>‹</button>
        <h2 className={styles.heading}>{year}년 {month}월 급식표</h2>
        <button className={styles.navBtn} onClick={nextMonth}>›</button>
      </div>

      {loading && <div className={styles.loadingBar} />}

      <div className={styles.calendar}>
        {WEEK_LABELS.map(d => (
          <div key={d} className={`${styles.weekday} ${d === '일' ? styles.sunLabel : d === '토' ? styles.satLabel : ''}`}>{d}</div>
        ))}
        {cells.map((date, i) => {
          if (!date) return <div key={`empty-${i}`} className={styles.emptyCell} />;
          const dayMeals = (grouped[date] ?? []).sort((a, b) => (MEAL_ORDER[a.mealType] ?? 9) - (MEAL_ORDER[b.mealType] ?? 9));
          const dow = new Date(date).getDay();
          const isSun = dow === 0;
          const isSat = dow === 6;
          const lunchMeal = dayMeals.find(m => m.mealType === '중식') ?? dayMeals[0];
          return (
            <div
              key={date}
              className={[
                styles.cell,
                date === today ? styles.todayCell : '',
                selected === date ? styles.selectedCell : '',
                isSun ? styles.sunCell : '',
                isSat ? styles.satCell : '',
              ].join(' ')}
              onClick={() => setSelected(selected === date ? null : date)}
            >
              <span className={styles.dayNum}>{Number(date.slice(8))}</span>
              {dayMeals.length > 0 && (
                <>
                  <div className={styles.dots}>
                    {dayMeals.map(m => (
                      <span key={m.id} title={m.mealType}>
                        {m.mealType === '조식' ? '🌅' : m.mealType === '중식' ? '🍚' : '🌙'}
                      </span>
                    ))}
                  </div>
                  {lunchMeal && (
                    <div className={styles.previewLine}>
                      {lunchMeal.dishNames.split('\n')[0]}
                    </div>
                  )}
                </>
              )}
            </div>
          );
        })}
      </div>

      {selected && (
        <div className={styles.detail}>
          <h3 className={styles.detailTitle}>
            {selected}{selected === today ? ' (오늘)' : ''}
          </h3>
          {sortedSelected.length === 0 ? (
            <p className={styles.noMeal}>급식 정보가 없습니다.</p>
          ) : (
            <div className={styles.detailGrid}>
              {sortedSelected.map(meal => (
                <div key={meal.id} className={styles.detailCard}>
                  <div className={styles.detailType}>
                    {meal.mealType === '조식' ? '🌅' : meal.mealType === '중식' ? '🍚' : '🌙'} {meal.mealType}
                  </div>
                  <ul className={styles.detailDishes}>
                    {meal.dishNames.split('\n').filter(Boolean).map((d, i) => (
                      <li key={i}>{d}</li>
                    ))}
                  </ul>
                  <div className={styles.detailCal}>{meal.calInfo}</div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
