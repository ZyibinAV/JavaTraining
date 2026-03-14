<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">

    <h2>Редактирование профиля</h2>

    <c:set var="user" value="${sessionScope.currentUser}"/>

    <form method="post" action="${pageContext.request.contextPath}/profile/edit">

        <div class="form-group">
            <label>
                Никнейм
                <input type="text"
                       name="nickname"
                       value="${user.nickname}"
                       required>
            </label>
        </div>

        <div class="form-group">
            <label>
                О себе
                <textarea name="about"
                          rows="5">${user.about}</textarea>
            </label>
        </div>

        <div class="form-actions">
            <button type="submit">Сохранить</button>
            <a href="${pageContext.request.contextPath}/profile">Отмена</a>
        </div>

    </form>

</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

