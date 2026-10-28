<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
%>
<h1>Enregistrer un employé</h1>
<form method="post" action="<%= contextPath %>/session/enregistrer">
    <input type="text" name="nom" placeholder="Nom" required><br>
    <input type="text" name="prenom" placeholder="Prénom"><br>
    <input type="number" name="age" placeholder="Âge"><br>
    <input type="text" name="matricule" placeholder="Matricule"><br>
    <button type="submit">Enregistrer</button>
</form>
