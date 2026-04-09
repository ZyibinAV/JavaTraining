<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">
    <div class="error-page">
        <div class="error-content">
            <div class="error-icon">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
            </div>
            <h1>Произошла ошибка</h1>
            <p class="error-message">${error}</p>
            <div class="error-actions">
                <a href="${pageContext.request.contextPath}/home" class="btn-primary">
                    Вернуться на главную
                </a>
            </div>
        </div>
    </div>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>
