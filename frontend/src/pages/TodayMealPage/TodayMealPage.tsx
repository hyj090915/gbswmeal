import { useTodayMeal } from '../../hooks/useMeal';
import { MealCard } from '../../components/MealCard/MealCard';
import { VoteButton } from '../../components/VoteButton/VoteButton';
import { CommentList } from '../../components/CommentList/CommentList';
import styles from './TodayMealPage.module.css';

interface Props {
  isLoggedIn: boolean;
}

export function TodayMealPage({ isLoggedIn }: Props) {
  const { meals, loading, error } = useTodayMeal();

  if (loading) return <div className={styles.center}>급식 정보를 불러오는 중...</div>;
  if (error) return <div className={styles.center}>{error}</div>;
  if (meals.length === 0) return <div className={styles.center}>오늘은 급식이 없습니다 🏖️</div>;

  return (
    <div className={styles.container}>
      <h2 className={styles.heading}>오늘의 급식</h2>
      {meals.map((meal) => (
        <div key={meal.id} className={styles.section}>
          <MealCard meal={meal} />
          <VoteButton mealId={meal.id} />
          <CommentList mealId={meal.id} isLoggedIn={isLoggedIn} />
        </div>
      ))}
    </div>
  );
}
