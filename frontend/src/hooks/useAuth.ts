import { useState, useEffect } from 'react';
import api from '../api';

interface AuthState {
  email: string | null;
  role: string | null;
  isLoggedIn: boolean;
}

export function useAuth() {
  const [auth, setAuth] = useState<AuthState>({
    email: null,
    role: null,
    isLoggedIn: false,
  });

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      api.get('/auth/me')
        .then((res) => {
          setAuth({ email: res.data.email, role: res.data.role, isLoggedIn: true });
        })
        .catch(() => {
          localStorage.removeItem('accessToken');
        });
    }
  }, []);

  const login = (token: string, email: string, role: string) => {
    localStorage.setItem('accessToken', token);
    setAuth({ email, role, isLoggedIn: true });
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    setAuth({ email: null, role: null, isLoggedIn: false });
  };

  return { ...auth, login, logout };
}
