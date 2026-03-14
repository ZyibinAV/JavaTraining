<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">

    <h2>Список пользователей</h2>

    <table class="data-table">
        <thead>
        <tr>
            <th>Логин</th>
            <th>Email</th>
            <th>Роль</th>
            <th>Статус</th>
            <th>Действия</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${user.role}</td>
                <td>
                    <c:choose>
                        <c:when test="${user.blocked}">
                            <span class="status-fail">Заблокирован</span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-success">Активен</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="user-actions">

                    <form method="post"
                          action="${pageContext.request.contextPath}/admin/users/role">
                        <input type="hidden" name="userId" value="${user.id}">
                        <select name="role">
                            <option value="USER">USER</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                        <button type="submit">Сменить роль</button>
                    </form>

                    <form method="post"
                          action="${pageContext.request.contextPath}/admin/users/block">
                        <input type="hidden" name="userId" value="${user.id}">
                        <button type="submit">
                            <c:choose>
                                <c:when test="${user.blocked}">
                                    Разблокировать
                                </c:when>
                                <c:otherwise>
                                    Заблокировать
                                </c:otherwise>
                            </c:choose>
                        </button>
                    </form>

                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

