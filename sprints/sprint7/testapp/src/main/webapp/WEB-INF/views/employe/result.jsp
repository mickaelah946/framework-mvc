<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    /** Echappe le texte saisi avant de l'afficher. */
    static String h(Object o) {
        if (o == null) return "";
        return o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<html>
<head><title>Resultat</title></head>
<body>
<h2>Ce que la methode a recu</h2>
<p>Methode appelee : <b><%= h(request.getAttribute("methode")) %></b></p>
<ul>
    <li>nom = <b><%= h(request.getAttribute("nom")) %></b></li>
    <% if (request.getAttribute("prenom") != null) { %><li>prenom = <b><%= h(request.getAttribute("prenom")) %></b></li><% } %>
    <% if (request.getAttribute("age") != null) { %><li>age = <b><%= h(request.getAttribute("age")) %></b></li><% } %>
</ul>
<p><a href="<%= request.getContextPath() %>/employe/form">Retour au formulaire</a></p>
</body>
</html>
