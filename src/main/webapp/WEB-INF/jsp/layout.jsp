<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <title>${pageTitle != null ? pageTitle : 'Java Interview Trainer'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<c:import url="/WEB-INF/jsp/common/header.jsp"/>

<main>
    <jsp:include page="${contentPage}"/>
</main>

<c:import url="/WEB-INF/jsp/common/footer.jsp"/>

</body>
</html>

