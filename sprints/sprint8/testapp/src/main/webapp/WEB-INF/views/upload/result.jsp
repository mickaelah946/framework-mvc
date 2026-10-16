<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.List" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Résultat de l'envoi</title>
<style>body{font-family:Arial,sans-serif;max-width:640px;margin:2rem auto;padding:0 1rem}.nul{color:#c00;font-style:italic}</style>
</head>
<body>
<%!
    static String h(Object o) {
        if (o == null || "null".equals(o.toString())) return "<span class=\"nul\">null</span>";
        return o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<h2>Fichiers reçus</h2>
<p>Méthode : <b><%= h(request.getAttribute("methode")) %></b></p>
<ul>
    <% for (Object ligne : (List<?>) request.getAttribute("lignes")) { %>
    <li><%= h(ligne) %></li>
    <% } %>
</ul>
<p><a href="<%= request.getContextPath() %>/upload/form">Retour</a></p>
</body>
</html>
