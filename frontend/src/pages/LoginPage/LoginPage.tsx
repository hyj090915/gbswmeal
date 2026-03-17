import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api';
import type { AuthResponse } from '../../types';
import styles from './LoginPage.module.css';

interface Props {
  onLogin: (token: string, email: string, role: string) => void;
}

export function LoginPage({ onLogin }: Props) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post<AuthResponse>('/auth/login', { email, password });
      localStorage.setItem('userEmail', res.data.email);
      onLogin(res.data.accessToken, res.data.email, res.data.role);
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.error ?? '로그인에 실패했습니다.');
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.box}>
        <h1 className={styles.title}>🍱 GBSWMeal</h1>
        <p className={styles.subtitle}>경북소프트웨어마이스터고 급식 평가</p>
        <form onSubmit={submit} className={styles.form}>
          <input
            className={styles.input}
            type="email"
            placeholder="학교 이메일 (@sc.gyo6.net)"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            className={styles.input}
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {error && <p className={styles.error}>{error}</p>}
          <button className={styles.btn} type="submit">로그인</button>
        </form>
        <p className={styles.link}>
          계정이 없으신가요? <Link to="/register">회원가입</Link>
        </p>
      </div>
    </div>
  );
}
