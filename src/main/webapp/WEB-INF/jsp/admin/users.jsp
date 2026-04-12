<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">

    <h2>Список пользователей</h2>

    <table class="data-table">
        <thead>
        <tr>
            <th>Пользователь</th>
            <th>Роль</th>
            <th>Статус</th>
            <th>Действия</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${users}">
            <tr>
                <td>
                    <div style="display: flex; align-items: center; gap: 12px;">
                        <img src="${pageContext.request.contextPath}${user.avatarPath}" alt="Avatar" style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid rgba(255, 255, 255, 0.3);">
                        <div>
                            <div style="font-weight: 600;">${user.username}</div>
                            <div style="font-size: 14px; color: #718096;">${user.email}</div>
                        </div>
                    </div>
                </td>
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
                    <div style="display: flex; gap: 8px; flex-wrap: wrap;">
                        <form method="post"
                              action="${pageContext.request.contextPath}/admin/users/role"
                              style="display: inline;">
                            <input type="hidden" name="userId" value="${user.id}">
                            <select name="role" style="padding: 6px 10px; border-radius: 8px; border: 1px solid #e2e8f0;">
                                <option value="USER" ${user.role == 'USER' ? 'selected' : ''}>USER</option>
                                <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                            </select>
                            <button type="submit" class="btn btn-sm btn-secondary">Роль</button>
                        </form>

                        <form method="post"
                              action="${pageContext.request.contextPath}/admin/users/block"
                              style="display: inline;">
                            <input type="hidden" name="userId" value="${user.id}">
                            <button type="submit" class="btn btn-sm btn-secondary">
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

                        <form method="post"
                              action="${pageContext.request.contextPath}/admin/users"
                              style="display: inline;"
                              onsubmit="return confirm('Удалить пользователя ${user.username}? Это действие нельзя отменить.');">
                            <input type="hidden" name="action" value="delete-user">
                            <input type="hidden" name="userId" value="${user.id}">
                            <button type="submit" class="btn-delete btn-sm">Удалить</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

