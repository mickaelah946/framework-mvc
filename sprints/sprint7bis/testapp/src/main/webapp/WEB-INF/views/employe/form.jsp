<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Employé</title>
<style>body{font-family:Arial,sans-serif;max-width:420px;margin:2rem auto;padding:0 1rem}input{display:block;margin:.3rem 0 1rem;padding:.4rem;width:100%;box-sizing:border-box}button{padding:.5rem 1.2rem}.nul{color:#c00;font-style:italic}</style>
</head>
<body>
<h2>Avec @Param</h2>
<form method="post" action="<%= request.getContextPath() %>/employe/saveParam">
    Nom <input type="text" name="nom">
    Prénom <input type="text" name="prenom">
    <button type="submit">Valider</button>
</form>
<h2>Avec un objet Employe</h2>
<form method="post" action="<%= request.getContextPath() %>/employe/saveObjet">
    Nom <input type="text" name="nom">
    Prénom <input type="text" name="prenom">
    Âge <input type="number" name="age">
    <button type="submit">Valider</button>
</form>
<h2>Objet dans un objet (adresse) et liste (langues)</h2>
<form method="post" action="<%= request.getContextPath() %>/employe/saveImbrique">
    Nom <input type="text" name="nom">
    Prénom <input type="text" name="prenom">
    Âge <input type="number" name="age">
    Rue <input type="text" name="adresse.rue">
    Ville <input type="text" name="adresse.ville">
    Langue 1 <input type="text" name="langues[0]">
    Langue 2 <input type="text" name="langues[1]">
    <button type="submit">Valider</button>
</form>
<h2>Liste d'objets (employes[0], employes[1])</h2>
<form method="post" action="<%= request.getContextPath() %>/employe/saveListe">
    Nom 1 <input type="text" name="employes[0].nom">
    Âge 1 <input type="number" name="employes[0].age">
    Nom 2 <input type="text" name="employes[1].nom">
    Âge 2 <input type="number" name="employes[1].age">
    <button type="submit">Valider</button>
</form>
<h2>Deux objets (employe et departement)</h2>
<form method="post" action="<%= request.getContextPath() %>/employe/saveDeux">
    Nom employé <input type="text" name="employe.nom">
    Âge employé <input type="number" name="employe.age">
    Nom département <input type="text" name="departement.nom">
    Effectif <input type="number" name="departement.effectif">
    <button type="submit">Valider</button>
</form>
</body>
</html>
