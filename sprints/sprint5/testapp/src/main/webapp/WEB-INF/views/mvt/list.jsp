<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.List,com.app.model.Mvt" %>
<%
    List<Mvt> mouvements = (List<Mvt>) request.getAttribute("mouvements");
    if (mouvements == null) {
        mouvements = java.util.Collections.emptyList();
    }
%>
<html>
<head><title>Liste des mouvements</title></head>
<body>
<h2>Liste des mouvements</h2>
<table border="1" cellpadding="6" cellspacing="0">
    <tr><th>ID</th><th>Libelle</th><th>Type</th><th>Montant</th></tr>
    <% for (Mvt m : mouvements) { %>
    <tr>
        <td><%= m.getId() %></td>
        <td><%= m.getLibelle() %></td>
        <td><%= m.getType() %></td>
        <td><%= m.getMontant() %></td>
    </tr>
    <% } %>
</table>
</body>
</html>