import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Link, Route, Routes, useNavigate, Navigate } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import './App.css';
import RegisterForm from "./components/RegisterForm";
import ChatbotInterface from "./components/ChatbotInterface";
import MainBoard from "./components/MainBoard";
import Settings from "./components/Settings";
import ReviewPage from "./components/ReviewPage";
import DashBoard from "./components/DashBoard";

function App() {
    return (
        <Router>
            <AppContent />
        </Router>
    );
}

function AppContent() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate();

    // 로컬 스토리지에서 토큰을 확인하여 로그인 상태를 관리
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setIsLoggedIn(true);
        }
    }, []);

    // 로그아웃 처리 및 리다이렉트
    const handleLogout = () => {
        localStorage.removeItem("token");
        setIsLoggedIn(false);
        navigate("/login"); // 로그아웃 후 로그인 페이지로 이동
    };

    return (
        <div className="App">
            <header className="App-header">
                <nav className="navbar">
                    <div className="navbar-left">
                        <Link to="/" className="navbar-brand">E-Commerce</Link>
                    </div>
                    <div className="navbar-right">
                        {isLoggedIn ? (
                            <>
                                {/* 로그아웃 버튼 */}
                                <button className="nav-link button" onClick={handleLogout}>로그아웃</button>
                                {/* 설정 페이지로 이동하는 링크 */}
                                <Link to="/mainboard" className="nav-link button">메인 화면</Link>
                            </>
                        ) : (
                            <>
                                {/* 로그인 및 회원가입 버튼 */}
                                <Link to="/login" className="nav-link button">로그인</Link>
                                <Link to="/register" className="nav-link button">회원가입</Link>
                            </>
                        )}
                    </div>
                </nav>

                {/* Routes 설정 */}
                <Routes>
                    {/* 루트 경로: 로그인 상태에 따라 이동 */}
                    <Route
                        path="/"
                        element={isLoggedIn ? <Navigate to="/mainboard" /> : <Navigate to="/login" />}
                    />

                    {/* 로그인 및 회원가입 */}
                    <Route path="/login" element={<LoginForm />} />
                    <Route path="/register" element={<RegisterForm />} />
                    <Route path="/mainboard" element={<MainBoard />} />
                    <Route path="/chatbot" element={<ChatbotInterface />} />
                    <Route path="/settings" element={<Settings />} />
                    <Route path="/review-items" element={<ReviewPage />} />
                    <Route path="/dashboard" element={<DashBoard />} />


                    {/* 404 처리 */}
                    <Route path="*" element={<Navigate to="/" />} />
                </Routes>
            </header>
        </div>
    );
}

export default App;