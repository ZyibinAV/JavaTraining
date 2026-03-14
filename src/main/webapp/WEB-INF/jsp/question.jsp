<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<div class="container">
    <div class="test-progress">
        <div class="progress-header">
            <h2>Вопрос ${questionNumber} из ${totalQuestions}</h2>
            <div class="progress-bar-container">
                <div class="progress-bar">
                    <div class="progress-fill" style="width: ${(questionNumber / totalQuestions) * 100}%"></div>
                </div>
                <span class="progress-text">${questionNumber}/${totalQuestions}</span>
            </div>
        </div>

        <div class="test-topics">
            <strong>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     style="display: inline-block; margin-right: 6px; vertical-align: middle;">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                </svg>
                Темы:
            </strong>
            <div class="topic-tags">
                <c:forEach var="topic" items="${topics}">
                    <span class="topic-tag">${topic.displayName}</span>
                </c:forEach>
            </div>
        </div>
    </div>

    <div class="content-card question-card">
        <div class="question-header">
            <span class="question-number">Вопрос ${questionNumber}</span>
        </div>
        <div class="question-body">
            <p class="question-text">
                ${question.questionText}
            </p>
        </div>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/question" class="answer-form">
        <div class="answers-container">
            <c:forEach var="answer" items="${question.answers}" varStatus="status">
                <label class="answer-option-modern">
                    <input type="radio"
                           name="answerIndex"
                           value="${status.index}"
                           required/>
                    <div class="answer-content">
                        <div class="answer-checkbox">
                            <svg class="checkmark" viewBox="0 0 24 24">
                                <path class="checkmark-path" d="M5 12l5 5L20 7"></path>
                            </svg>
                        </div>
                        <span class="answer-text">${answer}</span>
                    </div>
                    <div class="answer-indicator"></div>
                </label>
            </c:forEach>
        </div>

        <div class="question-actions">
            <button type="submit" class="btn-primary btn-large">
                <span>Ответить</span>
                <span class="button-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                        <polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
                </span>
            </button>
        </div>
    </form>
</div>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

