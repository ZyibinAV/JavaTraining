<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <h2>Вход в систему</h2>
            <p class="auth-subtitle">Java Interview Trainer</p>
        </div>

        <c:if test="${not empty error}">
            <div class="error-alert">
                <span class="error-icon">⚠️</span>
                    ${error}
            </div>
        </c:if>

        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group-modern">
                <label for="username">Логин</label>
                <div class="input-wrapper">
                    <input type="text" id="username" name="username" required
                           placeholder="Введите ваш логин" autocomplete="username">
                    <span class="input-icon">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </span>
                </div>
            </div>

            <div class="form-group-modern">
                <label for="password">Пароль</label>
                <div class="input-wrapper">
                    <input type="password" id="password" name="password" required
                           placeholder="Введите ваш пароль" autocomplete="current-password">
                    <span class="input-icon">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2">
                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                        </svg>
                    </span>
                    <button type="button" class="password-toggle">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                            <circle cx="12" cy="12" r="3"></circle>
                        </svg>
                    </button>
                </div>
            </div>

            <button type="submit" class="auth-button">
                <span>Войти</span>
                <span class="button-icon">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2">
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                            <polyline points="12 5 19 12 12 19"></polyline>
                        </svg>
                    </span>
            </button>
        </form>

        <div class="auth-footer">
            <p>Нет аккаунта? <a href="${pageContext.request.contextPath}/register">Зарегистрируйтесь</a></p>
        </div>
    </div>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

