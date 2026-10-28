<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title><%= request.getAttribute("titre") %></title></head>
<body>
<h2><%= request.getAttribute("titre") %></h2>
<p>Controller : <b><%= request.getAttribute("controller") %></b></p>
<p>Methode : <b><%= request.getAttribute("methode") %></b></p>
<p>Methode HTTP : <b><%= request.getAttribute("httpMethod") %></b></p>
<p><a href="<%= request.getContextPath() %>/">Retour a l'accueil</a></p>
</body>
</html>
