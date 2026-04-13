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

            <div class="form-group-modern">
                <input type="file"
                       id="avatarFile"
                       name="avatar"
                       accept="image/*"
                       required
                       style="display: none;"
                       onchange="document.getElementById('fileName').textContent = this.files[0]?.name || ''">
                
                <div style="display: flex; gap: 16px; align-items: center;">
                    <button type="button" class="btn btn-secondary" onclick="document.getElementById('avatarFile').click()">
                        Выберите файл
                    </button>
                    <span id="fileName" style="color: #718096;"></span>
                    <button type="submit" class="btn btn-primary">Загрузить</button>
                </div>
            </div>
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
                <button type="submit" class="btn btn-secondary">Сохранить</button>
                <button type="button" class="btn btn-primary" onclick="window.location.href='${pageContext.request.contextPath}/profile'">Отмена</button>
            </div>

        </form>
    </div>

</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

