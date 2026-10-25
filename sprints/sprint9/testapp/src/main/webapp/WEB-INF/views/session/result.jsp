<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Object visites = request.getAttribute("visites");
    Object active = request.getAttribute("active");
    String contextPath = request.getContextPath();
%>
<!doctype html>
<html>
<head><meta charset="UTF-8"><title><%= request.getAttribute("titre") %></title></head>
<body>
<h1><%= request.getAttribute("titre") %></h1>
<p><%= request.getAttribute("message") %></p>
<p>Visites dans cette session : <strong><%= visites %></strong></p>
<p>Session active : <strong><%= active %></strong></p>
<p><a href="<%= contextPath %>/session/compteur">Incrémenter le compteur</a></p>
<p><a href="<%= contextPath %>/session/etat">Relire l'état de session</a></p>
<form method="post" action="<%= contextPath %>/session/fin">
    <button type="submit">Terminer la session</button>
</form>
<p><a href="<%= contextPath %>/">Accueil</a></p>
</body>
</html>
