import { useState } from 'react';
import api from '../../api';
import type { DislikeReason, MyVote, VoteType } from '../../types';
import styles from './VoteButton.module.css';

interface Props {
  mealId: number;
  initialVote?: MyVote | null;
  onVoted?: () => void;
}

const DISLIKE_REASONS: { value: DislikeReason; label: string }[] = [
  { value: 'SALTY', label: '너무 짜요' },
  { value: 'SPICY', label: '너무 매워요' },
  { value: 'TASTELESS', label: '맛이 없어요' },
  { value: 'COLD', label: '식었어요' },
  { value: 'PORTION_SMALL', label: '양이 너무 적어요' },
  { value: 'OTHER', label: '기타' },
];

export function VoteButton({ mealId, initialVote, onVoted }: Props) {
  const [myVote, setMyVote] = useState<MyVote | null>(initialVote ?? null);
  const [showReasons, setShowReasons] = useState(false);

  const vote = async (voteType: VoteType, dislikeReason?: DislikeReason) => {
    try {
      await api.post('/votes', { mealId, voteType, dislikeReason });
      if (myVote?.voteType === voteType) {
        setMyVote(null);
      } else {
        setMyVote({ voteType, dislikeReason });
      }
      setShowReasons(false);
      onVoted?.();
    } catch (e) {
      alert('로그인이 필요합니다.');
    }
  };

  const handleDislike = () => {
    if (myVote?.voteType === 'DISLIKE') {
      vote('DISLIKE');
    } else {
      setShowReasons(true);
    }
  };

  return (
    <div className={styles.wrapper}>
      <button
        className={`${styles.btn} ${myVote?.voteType === 'LIKE' ? styles.active : ''}`}
        onClick={() => vote('LIKE')}
      >
        👍 좋아요
      </button>
      <button
        className={`${styles.btn} ${styles.dislike} ${myVote?.voteType === 'DISLIKE' ? styles.active : ''}`}
        onClick={handleDislike}
      >
        👎 싫어요
      </button>

      {showReasons && (
        <div className={styles.reasons}>
          <p>싫어요 이유를 선택해 주세요</p>
          {DISLIKE_REASONS.map((r) => (
            <button key={r.value} className={styles.reasonBtn} onClick={() => vote('DISLIKE', r.value)}>
              {r.label}
            </button>
          ))}
          <button className={styles.cancelBtn} onClick={() => setShowReasons(false)}>취소</button>
        </div>
      )}
    </div>
  );
}
