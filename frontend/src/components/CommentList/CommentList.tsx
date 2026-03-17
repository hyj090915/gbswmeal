import { useState, useEffect } from 'react';
import api from '../../api';
import type { Comment } from '../../types';
import styles from './CommentList.module.css';

interface Props {
  mealId: number;
  isLoggedIn: boolean;
}

export function CommentList({ mealId, isLoggedIn }: Props) {
  const [comments, setComments] = useState<Comment[]>([]);
  const [content, setContent] = useState('');
  const [rating, setRating] = useState(5);

  const fetchComments = () => {
    api.get<Comment[]>(`/comments/${mealId}`).then((res) => setComments(res.data));
  };

  useEffect(() => { fetchComments(); }, [mealId]);

  const submit = async () => {
    if (!content.trim()) return;
    await api.post(`/comments/${mealId}`, { content, rating });
    setContent('');
    setRating(5);
    fetchComments();
  };

  const deleteComment = async (id: number) => {
    await api.delete(`/comments/${id}`);
    fetchComments();
  };

  const myEmail = localStorage.getItem('userEmail');

  return (
    <div className={styles.wrapper}>
      <h3 className={styles.title}>댓글 ({comments.length})</h3>

      {isLoggedIn && (
        <div className={styles.form}>
          <div className={styles.stars}>
            {[1,2,3,4,5].map((s) => (
              <button key={s} className={`${styles.star} ${s <= rating ? styles.on : ''}`}
                onClick={() => setRating(s)}>★</button>
            ))}
          </div>
          <textarea
            className={styles.textarea}
            placeholder="급식 평가를 남겨주세요"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={3}
          />
          <button className={styles.submitBtn} onClick={submit}>등록</button>
        </div>
      )}

      <ul className={styles.list}>
        {comments.map((c) => (
          <li key={c.id} className={styles.item}>
            <div className={styles.meta}>
              <span className={styles.email}>{c.userEmail}</span>
              <span className={styles.commentRating}>{'★'.repeat(c.rating)}{'☆'.repeat(5 - c.rating)}</span>
              <span className={styles.date}>{new Date(c.createdAt).toLocaleDateString('ko-KR')}</span>
            </div>
            <p className={styles.content}>{c.content}</p>
            {myEmail === c.userEmail && (
              <button className={styles.deleteBtn} onClick={() => deleteComment(c.id)}>삭제</button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
