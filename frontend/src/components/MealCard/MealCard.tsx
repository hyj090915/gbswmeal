import type { Meal } from '../../types';
import styles from './MealCard.module.css';

interface Props {
  meal: Meal;
}

export function MealCard({ meal }: Props) {
  const dishes = meal.dishNames?.split('\n').filter(Boolean) ?? [];

  return (
    <div className={styles.card}>
      <div className={styles.header}>
        <span className={styles.type}>{meal.mealType}</span>
        <span className={styles.date}>{meal.mealDate}</span>
      </div>
      <ul className={styles.dishes}>
        {dishes.map((dish, i) => (
          <li key={i}>{dish}</li>
        ))}
      </ul>
      <div className={styles.footer}>
        <span className={styles.cal}>{meal.calInfo}</span>
        <span>👍 {meal.likeCount} / 👎 {meal.dislikeCount}</span>
      </div>
    </div>
  );
}
