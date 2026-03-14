<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">

    <h2>Аватар профиля</h2>

    <div class="avatar-section">
        <h3>Загрузить свою аватарку</h3>

        <form method="post"
              action="${pageContext.request.contextPath}/profile/avatar/upload"
              enctype="multipart/form-data">

            <div class="form-group">
                <input type="file"
                       name="avatar"
                       accept="image/*"
                       required>
            </div>

            <button type="submit">Загрузить</button>
        </form>
    </div>

    <div class="avatar-section">
        <h3>Выбрать из доступных</h3>

        <form method="post" action="${pageContext.request.contextPath}/profile/avatar">

            <div class="avatar-grid">
                <c:forEach var="avatar" items="${avatars}">
                    <label class="avatar-option">
                        <input type="radio"
                               name="avatarPath"
                               value="${avatar}"
                               required>
                        <img src="${pageContext.request.contextPath}${avatar}"
                             alt="avatar">
                    </label>
                </c:forEach>
            </div>

            <div class="avatar-actions">
                <button type="submit">Сохранить</button>
                <a href="${pageContext.request.contextPath}/profile">Отмена</a>
            </div>

        </form>
    </div>

</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

