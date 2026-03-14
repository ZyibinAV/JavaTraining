<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">
    <div class="welcome-section">
        <h2>Добро пожаловать в Java Interview Trainer</h2>

        <div class="content-card">
            <div class="hero-content">
                <div class="hero-text">
                    <h3>Подготовка к собеседованиям по Java</h3>
                    <p class="hero-description">
                        Это современное учебное веб-приложение для эффективной подготовки
                        к техническим собеседованиям. Тренируйтесь в формате, близком к реальному интервью.
                    </p>
                </div>
                <div class="hero-features">
                    <div class="feature-item">
                        <span class="feature-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2">
                                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                            </svg>
                        </span>
                        <div>
                            <strong>Тематические тесты</strong>
                            <p>Вопросы по ключевым темам Java</p>
                        </div>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2">
                                <path d="M21 21l-6-6m2-5a7 7 0 1 1-14 0 7 7 0 0 1 14 0z"></path>
                            </svg>
                        </span>
                        <div>
                            <strong>Случайная генерация</strong>
                            <p>Уникальный набор вопросов каждый раз</p>
                        </div>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2">
                                <line x1="18" y1="20" x2="18" y2="10"></line>
                                <line x1="12" y1="20" x2="12" y2="4"></line>
                                <line x1="6" y1="20" x2="6" y2="14"></line>
                            </svg>
                        </span>
                        <div>
                            <strong>Статистика прогресса</strong>
                            <p>Отслеживайте свой уровень подготовки</p>
                        </div>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"></polygon>
                            </svg>
                        </span>
                        <div>
                            <strong>Реалистичный формат</strong>
                            <p>Тренировка в условиях интервью</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="cta-section">
            <form method="get" action="${pageContext.request.contextPath}/test/settings">
                <button type="submit" class="btn-primary btn-large">
                    <span>Начать подготовку</span>
                    <span class="button-icon">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2">
                            <path d="M4.5 16.5c0 1.1.9 2 2 2s2-.9 2-2-.9-2-2-2-2 .9-2 2zm0-9c0 1.1.9 2 2 2s2-.9 2-2-.9-2-2-2-2 .9-2 2zm9 0c0 1.1.9 2 2 2s2-.9 2-2-.9-2-2-2-2 .9-2 2z"></path>
                        </svg>
                    </span>
                </button>
            </form>
        </div>
    </div>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

