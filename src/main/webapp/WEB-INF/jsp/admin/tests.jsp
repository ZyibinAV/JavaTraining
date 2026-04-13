<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">

    <h2>Управление тестами</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error">
            ${error}
        </div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success">
            ${success}
        </div>
    </c:if>

    <div class="content-card">
        <h3>Добавить новую тему</h3>
        <form method="post" action="${pageContext.request.contextPath}/admin/tests">
            <input type="hidden" name="action" value="add-topic">
            <div class="form-group-modern">
                <label for="code">Код темы (English, без пробелов)</label>
                <div class="input-wrapper">
                    <input type="text" id="code" name="code" required pattern="[a-z0-9-]+" 
                           placeholder="например: java-core">
                </div>
            </div>
            <div class="form-group-modern">
                <label for="displayName">Отображаемое название</label>
                <div class="input-wrapper">
                    <input type="text" id="displayName" name="displayName" required 
                           placeholder="например: Java Core">
                </div>
            </div>
            <button type="submit" class="btn btn-primary">Добавить тему</button>
        </form>
    </div>

    <div class="content-card">
        <h3>Список тем</h3>
        <c:if test="${empty topics}">
            <p>Нет доступных тем</p>
        </c:if>
        <c:if test="${not empty topics}">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Код</th>
                    <th>Название</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="topic" items="${topics}">
                    <tr>
                        <td><strong>${topic.code}</strong></td>
                        <td>${topic.displayName}</td>
                        <td class="actions">
                            <a href="${pageContext.request.contextPath}/admin/tests?action=edit&topicCode=${topic.code}" 
                               class="btn btn-sm btn-secondary">
                                Вопросы
                            </a>
                            <form method="post" action="${pageContext.request.contextPath}/admin/tests" 
                                  style="display: inline;" onsubmit="return confirm('Удалить тему "${topic.displayName}"? Все вопросы будут удалены.');">
                                <input type="hidden" name="action" value="delete-topic">
                                <input type="hidden" name="topicCode" value="${topic.code}">
                                <button type="submit" class="btn-delete">Удалить</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>

    <div class="content-card">
        <h3>Инструкция по работе с тестами</h3>
        <div class="instructions">
            <h4>Способ 1: Через JSON файлы</h4>
            <ol>
                <li>Создайте JSON файл с вопросами для темы</li>
                <li>Формат файла: <code>questions/{код_темы}.json</code></li>
                <li>Перейдите в раздел "Вопросы" для темы</li>
                <li>Загрузите JSON файл через форму</li>
            </ol>

            <h4>Способ 2: Прямое редактирование в БД</h4>
            <ol>
                <li>Перейдите в раздел "Вопросы" для темы</li>
                <li>Редактируйте вопросы напрямую через интерфейс</li>
                <li>Добавляйте/изменяйте варианты ответов</li>
                <li>Указывайте индекс правильного ответа (0, 1, 2, ...)</li>
            </ol>

            <h4>Формат JSON файла:</h4>
            <pre class="code-example">
[
  {
    "questionText": "Текст вопроса?",
    "correctAnswerIndex": 0,
    "answers": [
      "Правильный ответ",
      "Неправильный ответ",
      "Неправильный ответ",
      "Неправильный ответ"
    ]
  }
]</pre>
        </div>
    </div>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>
