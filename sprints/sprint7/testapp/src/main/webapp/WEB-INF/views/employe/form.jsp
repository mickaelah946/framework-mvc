<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Employé</title>
<style>body{font-family:Arial,sans-serif;max-width:420px;margin:2rem auto;padding:0 1rem}input{display:block;margin:.3rem 0 1rem;padding:.4rem;width:100%;box-sizing:border-box}button{padding:.5rem 1.2rem}.nul{color:#c00;font-style:italic}</style>
</head>
<body>
<h2>Formulaire</h2>
<form method="post" action="<%= request.getContextPath() %>/employe/save">
    Nom <input type="text" name="nom">
    Prénom <input type="text" name="prenom">
    Âge <input type="number" name="age">
    <button type="submit">Valider</button>
</form>
</body>
</html>
