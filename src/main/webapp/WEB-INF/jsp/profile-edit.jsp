<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">

    <h2>Редактирование профиля</h2>

    <c:set var="user" value="${sessionScope.currentUser}"/>

    <c:if test="${not empty error}">
        <div class="modern-alert error">
            ${error}
        </div>
    </c:if>

    <div class="content-card">
        <form method="post" action="${pageContext.request.contextPath}/profile/edit">

            <div class="form-group-modern">
                <label>Никнейм</label>
                <div class="input-wrapper">
                    <input type="text"
                           name="nickname"
                           value="${user.nickname}"
                           required
                           placeholder="Введите ваш никнейм">
                </div>
            </div>

            <div class="form-group-modern">
                <label>О себе</label>
                <div class="input-wrapper">
                    <textarea name="about"
                              placeholder="Расскажите о себе">${user.about}</textarea>
                </div>
            </div>

            <div style="margin: 32px 0; padding-top: 32px; border-top: 1px solid #e2e8f0;">
                <h3 style="margin-bottom: 24px; font-size: 18px; color: #2d3748;">Смена пароля</h3>

                <div class="form-group-modern">
                    <label>Текущий пароль</label>
                    <div class="input-wrapper">
                        <input type="password"
                               name="currentPassword"
                               placeholder="Введите текущий пароль">
                    </div>
                </div>

                <div class="form-group-modern">
                    <label>Новый пароль</label>
                    <div class="input-wrapper">
                        <input type="password"
                               name="newPassword"
                               placeholder="Введите новый пароль">
                    </div>
                </div>

                <div class="form-group-modern">
                    <label>Подтверждение нового пароля</label>
                    <div class="input-wrapper">
                        <input type="password"
                               name="confirmPassword"
                               placeholder="Подтвердите новый пароль">
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-primary">Сохранить изменения</button>
                <a href="${pageContext.request.contextPath}/profile" class="btn-secondary">Отмена</a>
            </div>

        </form>
    </div>

</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

