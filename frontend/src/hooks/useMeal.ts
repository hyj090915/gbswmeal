import { useState, useEffect } from 'react';
import api from '../api';
import type { Meal } from '../types';

export function useTodayMeal() {
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.get<Meal[]>('/meals/today')
      .then((res) => setMeals(res.data))
      .catch(() => setError('급식 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, []);

  return { meals, loading, error };
}

export function useWeeklyMeal() {
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.get<Meal[]>('/meals/week')
      .then((res) => setMeals(res.data))
      .catch(() => setError('이번 주 급식 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, []);

  return { meals, loading, error };
}

export function useMonthlyMeal(year: number, month: number) {
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setMeals([]);
    api.get<Meal[]>(`/meals/month?year=${year}&month=${month}`)
      .then((res) => setMeals(res.data))
      .catch(() => setError('급식 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, [year, month]);

  return { meals, loading, error };
}
