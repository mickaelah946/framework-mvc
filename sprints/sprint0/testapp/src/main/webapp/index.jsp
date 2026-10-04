<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Sprint 0</title></head>
<body>
<h2>Sprint 0 : test du FrontControllerServlet</h2>
<p><a href="${pageContext.request.contextPath}/hello">Envoyer une requete GET vers /hello</a></p>
<form action="${pageContext.request.contextPath}/submit" method="post">
    <input type="submit" value="Envoyer une requete POST vers /submit">
</form>
</body>
</html>