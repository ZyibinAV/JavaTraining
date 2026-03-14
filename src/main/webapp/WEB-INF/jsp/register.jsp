<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <h2>Создание аккаунта</h2>
            <p class="auth-subtitle">Присоединяйтесь к Java Interview Trainer</p>
        </div>

        <c:if test="${not empty error}">
            <div class="error-alert">
                <span class="error-icon">⚠️</span>
                    ${error}
            </div>
        </c:if>

        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/register">
            <div class="form-group-modern">
                <label for="username">Логин</label>
                <div class="input-wrapper">
                    <input type="text" id="username" name="username" required
                           placeholder="Придумайте логин" minlength="3" maxlength="20"
                           title="Правила логина: от 3 до 20 символов, только буквы, цифры, подчеркивания и дефисы">
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
                <label for="email">Email</label>
                <div class="input-wrapper">
                    <input type="email" id="email" name="email" required
                           placeholder="your@email.com">
                    <span class="input-icon">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2">
                            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
                            <polyline points="22,6 12,13 2,6"></polyline>
                        </svg>
                    </span>
                </div>
            </div>

            <div class="form-group-modern">
                <label for="password">Пароль</label>
                <div class="input-wrapper">
                    <input type="password" id="password" name="password" required
                           placeholder="Минимум 6 символов" minlength="6">
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
                <span>Зарегистрироваться</span>
                <span class="button-icon">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2">
                            <polyline points="20 6 9 17 4 12"></polyline>
                        </svg>
                    </span>
            </button>
        </form>

        <div class="auth-footer">
            <p>Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login">Войдите</a></p>
        </div>
    </div>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

