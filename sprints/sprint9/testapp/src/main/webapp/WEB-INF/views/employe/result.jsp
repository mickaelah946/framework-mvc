<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Employé</title>
<style>body{font-family:Arial,sans-serif;max-width:420px;margin:2rem auto;padding:0 1rem}input{display:block;margin:.3rem 0 1rem;padding:.4rem;width:100%;box-sizing:border-box}button{padding:.5rem 1.2rem}.nul{color:#c00;font-style:italic}</style>
</head>
<body>
<%!
    static String h(Object o) {
        if (o == null || "null".equals(o.toString())) return "<span class=\"nul\">null</span>";
        if (o.toString().isEmpty()) return "<span class=\"nul\">vide</span>";
        return o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<h2>Résultat</h2>
<p>Nom : <b><%= h(request.getAttribute("nom")) %></b></p>
<% if (request.getAttribute("prenom") != null) { %><p>Prénom : <b><%= h(request.getAttribute("prenom")) %></b></p><% } %>
<% if (request.getAttribute("age") != null) { %><p>Âge : <b><%= h(request.getAttribute("age")) %></b></p><% } %>
<p><a href="<%= request.getContextPath() %>/employe/form">Retour</a></p>
</body>
</html>
