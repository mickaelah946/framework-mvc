<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Formulaire employe</title></head>
<body>
<h2>Formulaire employe (Sprint 7)</h2>
<form method="post" action="<%= request.getContextPath() %>/employe/save">
    <p>Nom : <input type="text" name="nom"></p>
    <p>Prenom : <input type="text" name="prenom"></p>
    <p>Age : <input type="number" name="age"></p>
    <p><button type="submit">Valider</button></p>
</form>
<p>Test en GET : <a href="<%= request.getContextPath() %>/employe/hello?nom=Mimi">/employe/hello?nom=Mimi</a>
 | <a href="<%= request.getContextPath() %>/employe/json?nom=Mimi&age=30">/employe/json?nom=Mimi&amp;age=30</a></p>
<p><a href="<%= request.getContextPath() %>/">Retour a l'accueil</a></p>
</body>
</html>
