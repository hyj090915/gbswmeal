import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api';
import styles from './RegisterPage.module.css';

export function RegisterPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    if (!email.endsWith('@sc.gyo6.net')) {
      setError('학교 이메일(@sc.gyo6.net)만 가입 가능합니다.');
      return;
    }
    try {
      await api.post('/auth/register', { email, password });
      alert('회원가입이 완료되었습니다. 로그인해 주세요.');
      navigate('/login');
    } catch (err: any) {
      setError(err.response?.data?.error ?? '회원가입에 실패했습니다.');
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.box}>
        <h1 className={styles.title}>🍱 회원가입</h1>
        <p className={styles.subtitle}>학교 이메일(@sc.gyo6.net)로 가입하세요</p>
        <form onSubmit={submit} className={styles.form}>
          <input
            className={styles.input}
            type="email"
            placeholder="학교 이메일 (예: 20001@sc.gyo6.net)"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            className={styles.input}
            type="password"
            placeholder="비밀번호 (6자 이상)"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={6}
          />
          {error && <p className={styles.error}>{error}</p>}
          <button className={styles.btn} type="submit">가입하기</button>
        </form>
        <p className={styles.link}>
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  );
}
