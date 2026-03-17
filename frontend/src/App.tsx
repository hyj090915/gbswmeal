import { BrowserRouter, Routes, Route, NavLink, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import { LoginPage } from './pages/LoginPage/LoginPage';
import { RegisterPage } from './pages/RegisterPage/RegisterPage';
import { TodayMealPage } from './pages/TodayMealPage/TodayMealPage';
import { WeeklyMealPage } from './pages/WeeklyMealPage/WeeklyMealPage';
import { SchedulePage } from './pages/SchedulePage/SchedulePage';
import { SuggestionPage } from './pages/SuggestionPage/SuggestionPage';
import styles from './App.module.css';

function App() {
  const { isLoggedIn, email, login, logout } = useAuth();

  return (
    <BrowserRouter>
      <nav className={styles.nav}>
        <div className={styles.navLeft}>
          <span className={styles.logo}>🍱 GBSWMeal</span>
          <NavLink to="/" className={({ isActive }) => isActive ? styles.activeLink : styles.link}>
            오늘 급식
          </NavLink>
          <NavLink to="/week" className={({ isActive }) => isActive ? styles.activeLink : styles.link}>
            주간 급식
          </NavLink>
          <NavLink to="/schedule" className={({ isActive }) => isActive ? styles.activeLink : styles.link}>
            전체 급식표
          </NavLink>
          <NavLink to="/suggestions" className={({ isActive }) => isActive ? styles.activeLink : styles.link}>
            메뉴 제안
          </NavLink>
        </div>
        <div className={styles.auth}>
          {isLoggedIn ? (
            <>
              <span className={styles.email}>{email}</span>
              <button className={styles.logoutBtn} onClick={logout}>로그아웃</button>
            </>
          ) : (
            <NavLink to="/login" className={styles.loginBtn}>로그인</NavLink>
          )}
        </div>
      </nav>

      <main>
        <Routes>
          <Route path="/" element={<TodayMealPage isLoggedIn={isLoggedIn} />} />
          <Route path="/week" element={<WeeklyMealPage />} />
          <Route path="/schedule" element={<SchedulePage />} />
          <Route path="/suggestions" element={<SuggestionPage isLoggedIn={isLoggedIn} />} />
          <Route path="/login" element={isLoggedIn ? <Navigate to="/" /> : <LoginPage onLogin={login} />} />
          <Route path="/register" element={isLoggedIn ? <Navigate to="/" /> : <RegisterPage />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;
