<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">

    <h2>Статистика тестов</h2>

    <div class="stats-summary">
        <p><strong>Всего тестов:</strong> ${totalTests}</p>
        <p><strong>Успешно пройдено:</strong> ${passedTests}</p>
    </div>

    <div class="stats-section">
        <h3>По пользователям</h3>

        <table class="data-table">
            <tr>
                <th>Пользователь</th>
                <th>Всего тестов</th>
                <th>Пройдено</th>
            </tr>
            <c:forEach var="stat" items="${userStats}">
                <tr>
                    <td>${stat.username}</td>
                    <td>${stat.total}</td>
                    <td>${stat.passed}</td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <div class="stats-section">
        <h3>По темам</h3>

        <table class="data-table">
            <tr>
                <th>Тема</th>
                <th>Попыток</th>
                <th>Успешно</th>
                <th>% успеха</th>
            </tr>
            <c:forEach var="stat" items="${topicStats}">
                <tr>
                    <td>${stat.topicDisplayName}</td>
                    <td>${stat.total}</td>
                    <td>${stat.passed}</td>
                    <td>${stat.successRate}</td>
                </tr>
            </c:forEach>
        </table>
    </div>

</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

