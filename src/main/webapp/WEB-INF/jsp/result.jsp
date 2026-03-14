<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">
    <div class="result-header">
        <h2>Результат теста</h2>
    </div>

    <div class="content-card result-stats">
        <div class="result-score">
            <div class="score-circle ${passed ? 'success' : 'fail'}">
                <span class="score-number">${correct}</span>
                <span class="score-total">из ${total}</span>
            </div>
            <div class="score-details">
                <h3>
                    <c:choose>
                        <c:when test="${passed}">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2"
                                 style="display: inline-block; margin-right: 8px; vertical-align: middle; color: #48bb78;">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                                <polyline points="22 4 12 14.01 9 11.01"></polyline>
                            </svg>
                            Тест пройден!
                        </c:when>
                        <c:otherwise>
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2"
                                 style="display: inline-block; margin-right: 8px; vertical-align: middle; color: #fc8181;">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="15" y1="9" x2="9" y2="15"></line>
                                <line x1="9" y1="9" x2="15" y2="15"></line>
                            </svg>
                            Тест не пройден
                        </c:otherwise>
                    </c:choose>
                </h3>
                <p class="result-description">
                    Правильных ответов: ${correct} из ${total} (${Math.round(correct * 100 / total)}%)
                </p>
                <div class="result-topics">
                    <strong>Темы:</strong>
                    <div class="topic-tags">
                        <c:forTokens items="${topics}" delims="," var="topic">
                            <span class="topic-tag">${topic.trim()}</span>
                        </c:forTokens>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="content-card result-recommendations">
        <h3>
            <c:choose>
                <c:when test="${passed}">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         style="display: inline-block; margin-right: 8px; vertical-align: middle;">
                        <circle cx="12" cy="12" r="10"></circle>
                        <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"></polygon>
                    </svg>
                    Рекомендации
                </c:when>
                <c:otherwise>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         style="display: inline-block; margin-right: 8px; vertical-align: middle;">
                        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                    </svg>
                    Что нужно улучшить
                </c:otherwise>
            </c:choose>
        </h3>
        <div class="recommendations-content">
            <c:choose>
                <c:when test="${passed}">
                    <div class="modern-alert success">
                        <span>
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2"
                                 style="display: inline-block; margin-right: 6px; vertical-align: middle;">
                                <circle cx="12" cy="12" r="10"></circle>
                                <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"></polygon>
                            </svg>
                            Отличный результат! Вы готовы к собеседованию по этим темам.
                        </span>
                    </div>
                    <p class="recommendation-text">
                        Поздравляем! Вы успешно прошли тест. Рекомендуем повторить материал через неделю
                        для закрепления знаний и попробовать более сложные темы.
                    </p>
                </c:when>
                <c:otherwise>
                    <div class="modern-alert error">
                        <span>
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2"
                                 style="display: inline-block; margin-right: 6px; vertical-align: middle;">
                                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                            </svg>
                            Нужно больше практики по этим темам.
                        </span>
                    </div>
                    <p class="recommendation-text">
                        Это учебный проект. Не расстраивайтесь, если тест не пройден с первого раза.
                        Рекомендуем изучить теорию по слабым темам и попробовать пройти тест снова.
                    </p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="result-actions">
        <a href="${pageContext.request.contextPath}/home" class="btn-secondary">
            <span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     style="display: inline-block; margin-right: 6px; vertical-align: middle;">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                    <polyline points="9 22 9 12 13 12"></polyline>
                </svg>
                На главную
            </span>
        </a>
        <a href="${pageContext.request.contextPath}/test/settings" class="btn-primary">
            <span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     style="display: inline-block; margin-right: 6px; vertical-align: middle;">
                    <polyline points="23 4 23 10 17 10"></polyline>
                    <polyline points="1 20 1 14 7 14"></polyline>
                    <path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15m7.5-13.5L14 6l-3 3"></path>
                </svg>
                Новый тест
            </span>
        </a>
        <a href="${pageContext.request.contextPath}/profile" class="btn-secondary">
            <span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     style="display: inline-block; margin-right: 6px; vertical-align: middle;">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="8.5" cy="7" r="4"></circle>
                    <path d="M20 8v6M23 11v2"></path>
                </svg>
                Мой профиль
            </span>
        </a>
    </div>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

