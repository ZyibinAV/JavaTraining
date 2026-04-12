<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">
    <div class="admin-header">
        <h2>Вопросы: ${topic.displayName}</h2>
        <p class="admin-description">
            Код темы: ${topic.code} | Всего вопросов: ${questions.size()}
        </p>
        <a href="${pageContext.request.contextPath}/admin/tests" class="btn btn-secondary">
            ← Вернуться к списку тем
        </a>
    </div>

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

    <c:if test="${not editMode}">
        <div class="content-card">
            <h3>Загрузить вопросы из JSON файла</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/tests" 
                  enctype="multipart/form-data">
                <input type="hidden" name="action" value="upload-json">
                <input type="hidden" name="topicCode" value="${topic.code}">
                <div class="form-group-modern">
                    <label for="jsonFile">Выберите JSON файл</label>
                    <input type="file" id="jsonFile" name="jsonFile" accept=".json" required>
                    <small class="form-hint">Формат: массив объектов с полями questionText, correctAnswerIndex, answers</small>
                </div>
                <button type="submit" class="btn btn-primary">Загрузить JSON</button>
            </form>
        </div>

        <div class="content-card">
            <h3>Список вопросов</h3>
            <c:if test="${empty questions}">
                <p>Нет вопросов для этой темы</p>
            </c:if>
            <c:if test="${not empty questions}">
                <c:forEach var="question" items="${questions}" varStatus="status">
                    <div class="question-card">
                        <div class="question-header">
                            <h4>Вопрос #${status.index + 1}</h4>
                            <span class="badge">ID: ${question.id}</span>
                        </div>
                        <div class="question-text">
                            ${question.questionText}
                        </div>
                        <div class="answers-list">
                            <c:forEach var="answer" items="${question.answers}" varStatus="ansStatus">
                                <div class="answer-item ${ansStatus.index eq question.correctAnswerIndex ? 'correct' : ''}">
                                    <span class="answer-index">${ansStatus.index}.</span>
                                    ${answer.answerText}
                                    <c:if test="${ansStatus.index eq question.correctAnswerIndex}">
                                        <span class="correct-badge">✓ Правильный</span>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="question-actions">
                            <button class="btn btn-sm btn-secondary" onclick="editQuestion(${question.id})">
                                Редактировать
                            </button>
                            <button class="btn-delete" onclick="deleteQuestion(${question.id})">
                                Удалить
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </div>

        <div class="content-card">
            <h3>Добавить новый вопрос</h3>
            <form id="addQuestionForm" method="post" action="${pageContext.request.contextPath}/admin/tests">
                <input type="hidden" name="action" value="add-question">
                <input type="hidden" name="topicCode" value="${topic.code}">
                <div class="form-group-modern">
                    <label for="questionText">Текст вопроса</label>
                    <div class="input-wrapper">
                        <textarea id="questionText" name="questionText" rows="3" required></textarea>
                    </div>
                </div>
                <div class="form-group-modern">
                    <label for="correctAnswerIndex">Индекс правильного ответа (0, 1, 2, ...)</label>
                    <div class="input-wrapper">
                        <input type="number" id="correctAnswerIndex" name="correctAnswerIndex" min="0" value="0" required>
                    </div>
                </div>
                <div class="form-group-modern">
                    <label>Варианты ответов</label>
                    <div id="answersContainer">
                        <div class="answer-input-group">
                            <input type="text" name="answer0" placeholder="Вариант 0" required>
                        </div>
                        <div class="answer-input-group">
                            <input type="text" name="answer1" placeholder="Вариант 1" required>
                        </div>
                        <div class="answer-input-group">
                            <input type="text" name="answer2" placeholder="Вариант 2" required>
                        </div>
                        <div class="answer-input-group">
                            <input type="text" name="answer3" placeholder="Вариант 3" required>
                        </div>
                    </div>
                    <button type="button" class="btn btn-sm btn-secondary" onclick="addAnswerField()">+ Добавить вариант</button>
                </div>
                <button type="submit" class="btn btn-primary">Добавить вопрос</button>
            </form>
        </div>
    </c:if>

    <c:if test="${editMode}">
        <div class="content-card">
            <h3>Редактировать вопрос</h3>
            <form id="editQuestionForm" method="post" action="${pageContext.request.contextPath}/admin/tests">
                <input type="hidden" name="action" value="update-question">
                <input type="hidden" name="questionId" value="${question.id}">
                <input type="hidden" name="topicCode" value="${topic.code}">
                <div class="form-group-modern">
                    <label for="questionText">Текст вопроса</label>
                    <div class="input-wrapper">
                        <textarea id="questionText" name="questionText" rows="3" required>${question.questionText}</textarea>
                    </div>
                </div>
                <div class="form-group-modern">
                    <label for="correctAnswerIndex">Индекс правильного ответа (0, 1, 2, ...)</label>
                    <div class="input-wrapper">
                        <input type="number" id="correctAnswerIndex" name="correctAnswerIndex" min="0" value="${question.correctAnswerIndex}" required>
                    </div>
                </div>
                <div class="form-group-modern">
                    <label>Варианты ответов</label>
                    <div id="answersContainer">
                        <c:forEach var="answer" items="${question.answers}" varStatus="ansStatus">
                            <div class="answer-input-group">
                                <input type="text" name="answer${ansStatus.index}" placeholder="Вариант ${ansStatus.index}" value="${answer.answerText}" required>
                            </div>
                        </c:forEach>
                    </div>
                    <button type="button" class="btn btn-sm btn-secondary" onclick="addAnswerField()">+ Добавить вариант</button>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Сохранить изменения</button>
                    <a href="${pageContext.request.contextPath}/admin/tests?action=edit&topicCode=${topic.code}" class="btn btn-secondary">Отмена</a>
                </div>
            </form>
        </div>
    </c:if>
</div>

<script>
let answerCount = ${editMode ? question.answers.size() : 4};

function addAnswerField() {
    const container = document.getElementById('answersContainer');
    const newDiv = document.createElement('div');
    newDiv.className = 'answer-input-group';
    newDiv.innerHTML = `<input type="text" name="answer${answerCount}" placeholder="Вариант ${answerCount}" required>`;
    container.appendChild(newDiv);
    answerCount++;
}

function editQuestion(questionId) {
    window.location.href = '${pageContext.request.contextPath}/admin/tests?action=edit-question&questionId=' + questionId + '&topicCode=${topic.code}';
}

function deleteQuestion(questionId) {
    if (confirm('Удалить вопрос #' + questionId + '?')) {
        const form = document.createElement('form');
        form.method = 'post';
        form.action = '${pageContext.request.contextPath}/admin/tests';
        
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'delete-question';
        
        const questionIdInput = document.createElement('input');
        questionIdInput.type = 'hidden';
        questionIdInput.name = 'questionId';
        questionIdInput.value = questionId;
        
        const topicCodeInput = document.createElement('input');
        topicCodeInput.type = 'hidden';
        topicCodeInput.name = 'topicCode';
        topicCodeInput.value = '${topic.code}';
        
        form.appendChild(actionInput);
        form.appendChild(questionIdInput);
        form.appendChild(topicCodeInput);
        
        document.body.appendChild(form);
        form.submit();
    }
}
</script>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>
