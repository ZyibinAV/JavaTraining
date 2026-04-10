<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

<header class="app-header">
    <div class="header-row">
        <div>
            <a href="${pageContext.request.contextPath}/home"><strong>Java Interview Trainer</strong></a>
            |
            <a href="${pageContext.request.contextPath}/home">Главная</a>

            <c:if test="${not empty currentUser}">
                |
                <a href="${pageContext.request.contextPath}/profile">Профиль</a>
                |
                <a href="${pageContext.request.contextPath}/test/settings">Тесты</a>
            </c:if>

            <c:if test="${not empty currentUser && currentUser.role == 'ADMIN'}">
                |
                <a href="${pageContext.request.contextPath}/admin">Админка</a>
            </c:if>
        </div>

        <div style="display: flex; align-items: center; gap: 12px;">
            <c:if test="${not empty currentUser}">
                <div class="user-info-header" style="display: flex; align-items: center; gap: 8px;">
                    <img src="${pageContext.request.contextPath}${currentUser.avatarPath}" alt="Avatar" class="header-avatar" style="width: 32px; height: 32px; border-radius: 50%; object-fit: cover; border: 2px solid rgba(255, 255, 255, 0.3);">
                    <span style="color: #ffffff; font-weight: 600;">${currentUser.nickname}</span>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/logout" class="logout-form" style="display: inline; margin: 0; padding: 0;">
                    <button type="submit" class="logout-button" style="background: none; border: none; color: #ffffff; text-decoration: none; cursor: pointer; font-size: inherit; font-family: inherit; padding: 0; margin: 0;">Выйти</button>
                </form>
            </c:if>

            <c:if test="${empty currentUser}">
                <a href="${pageContext.request.contextPath}/login">Войти</a>
                |
                <a href="${pageContext.request.contextPath}/register">Регистрация</a>
            </c:if>
        </div>
    </div>
</header>
