<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Formulaire employe</title></head>
<body>
<h2>Formulaire employe (Sprint 7 et 7bis)</h2>
<form method="post" action="<%= request.getContextPath() %>/employe/save">
    <p>Nom : <input type="text" name="nom"></p>
    <p>Prenom : <input type="text" name="prenom"></p>
    <p>Age : <input type="number" name="age"></p>
    <p><button type="submit">Valider</button></p>
</form>
<h3>Sprint 7bis : @Param (champs nom et prenom dans lastName et firstName)</h3>
<form method="post" action="<%= request.getContextPath() %>/employe/saveParam">
    <p>Nom : <input type="text" name="nom"></p>
    <p>Prenom : <input type="text" name="prenom"></p>
    <p><button type="submit">Valider @Param</button></p>
</form>
<h3>Sprint 7bis : objet Employe rempli automatiquement</h3>
<form method="post" action="<%= request.getContextPath() %>/employe/saveObjet">
    <p>Nom : <input type="text" name="nom"> 
    <p>Prenom : <input type="text" name="prenom"><p>
    <p>Age : <input type="number" name="age"></p>
    <p><button type="submit">Valider objet</button></p>
</form>
<p>Test en GET : <a href="<%= request.getContextPath() %>/employe/hello?nom=Mimi">/employe/hello?nom=Mimi</a>
 | <a href="<%= request.getContextPath() %>/employe/json?nom=Mimi&age=30">/employe/json?nom=Mimi&amp;age=30</a>
 | <a href="<%= request.getContextPath() %>/employe/objet?nom=Mimi&prenom=Rakoto&age=30">/employe/objet?nom=Mimi&amp;prenom=Rakoto&amp;age=30</a></p>
<p><a href="<%= request.getContextPath() %>/">Retour a l'accueil</a></p>
</body>
</html>
