export interface Meal {
  id: number;
  mealDate: string;
  mealType: string;
  dishNames: string;
  calInfo: string;
  ntrInfo?: string;
  likeCount: number;
  dislikeCount: number;
}

export type VoteType = 'LIKE' | 'DISLIKE';
export type DislikeReason = 'SALTY' | 'SPICY' | 'TASTELESS' | 'COLD' | 'PORTION_SMALL' | 'OTHER';

export interface Vote {
  mealId: number;
  voteType: VoteType;
  dislikeReason?: DislikeReason;
}

export interface MyVote {
  voteType: VoteType;
  dislikeReason?: DislikeReason;
}

export interface Comment {
  id: number;
  mealId: number;
  userEmail: string;
  content: string;
  rating: number;
  createdAt: string;
}

export interface MenuSuggestion {
  id: number;
  title: string;
  description: string;
  userEmail: string;
  voteCount: number;
  deadline: string;
  createdAt: string;
  myVote: boolean;
}

export interface AuthResponse {
  accessToken: string;
  email: string;
  role: string;
}
