<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">
    <div class="test-settings-header">
        <h2>Настройка теста</h2>
        <p class="test-settings-description">
            Выберите темы и количество вопросов для прохождения теста.
            Вопросы подбираются случайным образом из выбранной базы.
        </p>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/start" class="test-settings-form">
        <div class="content-card">
            <h3>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     style="display: inline-block; margin-right: 8px; vertical-align: middle;">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                </svg>
                Темы теста
            </h3>
            <div class="topics-grid">
                <c:forEach var="topic" items="${topics}">
                    <label class="topic-card">
                        <input type="checkbox" name="topics" value="${topic}">
                        <div class="topic-content">
                            <span class="topic-icon">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="2">
                                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                                </svg>
                            </span>
                            <span class="topic-name">${topic.displayName}</span>
                        </div>
                        <div class="topic-check">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2">
                                <polyline points="20 6 9 17 4 12"></polyline>
                            </svg>
                        </div>
                    </label>
                </c:forEach>
            </div>
        </div>

        <div class="content-card">
            <h3>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     style="display: inline-block; margin-right: 8px; vertical-align: middle;">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"></polygon>
                </svg>
                Количество вопросов
            </h3>
            <div class="question-count-options">
                <label class="count-option">
                    <input type="radio" name="questionCount" value="10" required>
                    <div class="count-content">
                        <span class="count-number">10</span>
                        <span class="count-label">Быстрый тест</span>
                    </div>
                </label>
                <label class="count-option">
                    <input type="radio" name="questionCount" value="20">
                    <div class="count-content">
                        <span class="count-number">20</span>
                        <span class="count-label">Стандартный</span>
                    </div>
                </label>
                <label class="count-option">
                    <input type="radio" name="questionCount" value="30">
                    <div class="count-content">
                        <span class="count-number">30</span>
                        <span class="count-label">Полный</span>
                    </div>
                </label>
            </div>
        </div>

        <div class="test-settings-actions">
            <button type="submit" class="btn-primary btn-large">
                <span>Начать тестирование</span>
                <span class="button-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M4.5 16.5c0 1.1.9 2 2 2s2-.9 2-2-.9-2-2-2-2 .9-2 2zm0-9c0 1.1.9 2 2 2s2-.9 2-2-.9-2-2-2-2 .9-2 2zm9 0c0 1.1.9 2 2 2s2-.9 2-2-.9-2-2-2-2 .9-2 2z"></path>
                    </svg>
                </span>
            </button>
        </div>
    </form>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

