import { useState, useEffect } from 'react';
import api from '../../api';
import type { MenuSuggestion } from '../../types';
import styles from './SuggestionPage.module.css';

interface Props {
  isLoggedIn: boolean;
}

export function SuggestionPage({ isLoggedIn }: Props) {
  const [suggestions, setSuggestions] = useState<MenuSuggestion[]>([]);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [showForm, setShowForm] = useState(false);

  const fetchSuggestions = () => {
    api.get<MenuSuggestion[]>('/suggestions').then((res) => setSuggestions(res.data));
  };

  useEffect(() => { fetchSuggestions(); }, []);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    await api.post('/suggestions', { title, description });
    setTitle('');
    setDescription('');
    setShowForm(false);
    fetchSuggestions();
  };

  const toggleVote = async (suggestion: MenuSuggestion) => {
    if (suggestion.myVote) {
      await api.delete(`/suggestions/${suggestion.id}/vote`);
    } else {
      await api.post(`/suggestions/${suggestion.id}/vote`);
    }
    fetchSuggestions();
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h2 className={styles.heading}>다음달 메뉴 제안</h2>
        {isLoggedIn && (
          <button className={styles.addBtn} onClick={() => setShowForm(!showForm)}>
            {showForm ? '취소' : '+ 제안하기'}
          </button>
        )}
      </div>

      {showForm && (
        <form onSubmit={submit} className={styles.form}>
          <input
            className={styles.input}
            placeholder="메뉴 이름"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
          <textarea
            className={styles.textarea}
            placeholder="제안 이유나 설명 (선택)"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
          />
          <button className={styles.submitBtn} type="submit">등록</button>
        </form>
      )}

      <ul className={styles.list}>
        {suggestions.map((s) => (
          <li key={s.id} className={styles.item}>
            <div className={styles.info}>
              <span className={styles.suggestionTitle}>{s.title}</span>
              {s.description && <p className={styles.desc}>{s.description}</p>}
              <span className={styles.author}>{s.userEmail}</span>
            </div>
            <button
              className={`${styles.voteBtn} ${s.myVote ? styles.voted : ''}`}
              onClick={() => isLoggedIn ? toggleVote(s) : alert('로그인이 필요합니다.')}
            >
              ❤️ {s.voteCount}
            </button>
          </li>
        ))}
        {suggestions.length === 0 && (
          <p className={styles.empty}>아직 제안이 없습니다. 첫 번째로 제안해 보세요!</p>
        )}
      </ul>
    </div>
  );
}
