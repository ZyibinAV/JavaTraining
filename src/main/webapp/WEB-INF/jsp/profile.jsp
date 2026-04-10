<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">
    <div class="profile-header">
        <h2>Профиль пользователя</h2>
    </div>

    <c:set var="user" value="${sessionScope.currentUser}"/>

    <div class="content-card">
        <div class="profile-info">
            <div class="profile-avatar-section">
                <img class="profile-avatar-large"
                     src="${pageContext.request.contextPath}${user.avatarPath}"
                     alt="Avatar"
                     onerror="this.src='${pageContext.request.contextPath}/resources/avatars/default/default.png'">
                <div class="profile-details">
                    <h3>${user.nickname}</h3>
                    <div class="profile-username-section">
                        <span class="profile-label">Логин:</span>
                        <span class="profile-username">${user.username}</span>
                    </div>
                    <p class="profile-email">${user.email}</p>
                    <div class="profile-actions">
                        <a href="${pageContext.request.contextPath}/profile/edit" class="btn-secondary">
                            <span>
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="2"
                                     style="display: inline-block; margin-right: 6px; vertical-align: middle;">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
                            </span>
                            <span>Редактировать профиль</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/profile/avatar" class="btn-secondary">
                            <span>
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="2"
                                     style="display: inline-block; margin-right: 6px; vertical-align: middle;">
                                    <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                                    <circle cx="8.5" cy="8.5" r="1.5"></circle>
                                    <polyline points="21 15 16 10 5 21"></polyline>
                                </svg>
                            </span>
                            <span>Изменить аватар</span>
                        </a>
                    </div>
                </div>
            </div>

            <div class="profile-about">
                <h4>О себе</h4>
                <p>
                    <c:choose>
                        <c:when test="${empty user.about}">
                            <em>Информация не указана</em>
                        </c:when>
                        <c:otherwise>
                            ${user.about}
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>
    </div>

    <div class="content-card">
        <h3>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 style="display: inline-block; margin-right: 8px; vertical-align: middle;">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
            </svg>
            История прохождения тестов
        </h3>

        <c:if test="${empty results}">
            <div class="modern-alert info">
                <span>ℹ️</span>
                <span>Вы еще не проходили тесты. Начните подготовку!</span>
            </div>
        </c:if>

        <c:if test="${not empty results}">
            <div class="table-responsive">
                <table class="modern-table">
                    <thead>
                    <tr>
                        <th>Тема</th>
                        <th>Вопросов</th>
                        <th>Правильных</th>
                        <th>Результат</th>
                        <th>Дата</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="result" items="${results}">
                        <tr>
                            <td><strong>${result.topicDisplayName}</strong></td>
                            <td>${result.totalQuestions}</td>
                            <td>${result.correctAnswers}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${result.passed}">
                                        <span class="status-badge success">
                                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
                                                 stroke="currentColor" stroke-width="3"
                                                 style="display: inline-block; margin-right: 4px;">
                                                <polyline points="20 6 9 17 4 12"></polyline>
                                            </svg>
                                            Пройден
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge fail">
                                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
                                                 stroke="currentColor" stroke-width="3"
                                                 style="display: inline-block; margin-right: 4px;">
                                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                                <line x1="6" y1="6" x2="18" y2="18"></line>
                                            </svg>
                                            Не пройден
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${result.formattedFinishedAt}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>

    <div class="content-card">
        <h3>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 style="display: inline-block; margin-right: 8px; vertical-align: middle;">
                <line x1="18" y1="20" x2="18" y2="10"></line>
                <line x1="12" y1="20" x2="12" y2="4"></line>
                <line x1="6" y1="20" x2="6" y2="14"></line>
            </svg>
            Успешность по темам
        </h3>

        <c:if test="${empty topicStats}">
            <div class="modern-alert info">
                <span>ℹ️</span>
                <span>Пока нет данных для анализа</span>
            </div>
        </c:if>

        <c:if test="${not empty topicStats}">
            <div class="stats-grid">
                <c:forEach var="stat" items="${topicStats}">
                    <div class="stat-card">
                        <div class="stat-header">
                            <h4>${stat.topicDisplayName}</h4>
                            <div class="stat-percentage ${stat.successRate >= 70 ? 'success' : stat.successRate >= 50 ? 'warning' : 'danger'}">
                                    ${stat.successRate}%
                            </div>
                        </div>
                        <div class="stat-details">
                            <div class="stat-item">
                                <span>Попыток:</span>
                                <strong>${stat.total}</strong>
                            </div>
                            <div class="stat-item">
                                <span>Успешных:</span>
                                <strong>${stat.passed}</strong>
                            </div>
                        </div>
                        <div class="stat-progress">
                            <div class="progress-bar">
                                <div class="progress-fill" style="width: ${stat.successRate}%"></div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>
    </div>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

