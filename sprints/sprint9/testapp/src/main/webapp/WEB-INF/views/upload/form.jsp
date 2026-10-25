<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Envoi de fichiers</title>
<style>body{font-family:Arial,sans-serif;max-width:420px;margin:2rem auto;padding:0 1rem}input{display:block;margin:.3rem 0 1rem;padding:.4rem;width:100%;box-sizing:border-box}button{padding:.5rem 1.2rem}</style>
</head>
<body>
<h2>Arguments MapFile</h2>
<form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/upload/saveFichiers">
    Nom <input type="text" name="nom">
    CIN recto <input type="file" name="rectoCIN">
    CIN verso <input type="file" name="versoCIN">
    <button type="submit">Envoyer</button>
</form>
<h2>Objet Employe (attributs MapFile)</h2>
<form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/upload/saveObjet">
    Nom <input type="text" name="nom">
    Prénom <input type="text" name="prenom">
    Âge <input type="number" name="age">
    CIN recto <input type="file" name="rectoCIN">
    CIN verso <input type="file" name="versoCIN">
    <button type="submit">Envoyer</button>
</form>
<h2>Map&lt;String, MapFile&gt; (tous les fichiers)</h2>
<form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/upload/saveMap">
    Fichier A <input type="file" name="fichierA">
    Fichier B <input type="file" name="fichierB">
    <button type="submit">Envoyer</button>
</form>
<h2>Map&lt;String, byte[]&gt; (octets)</h2>
<form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/upload/saveOctets">
    Fichier A <input type="file" name="fichierA">
    Fichier B <input type="file" name="fichierB">
    <button type="submit">Envoyer</button>
</form>
<h2>List&lt;MapFile&gt; (plusieurs fichiers, même champ)</h2>
<form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/upload/saveListe">
    Nom <input type="text" name="nom">
    Photos <input type="file" name="photos" multiple>
    <button type="submit">Envoyer</button>
</form>
<h2>JSON (@APIREST)</h2>
<form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/upload/info">
    Fichier A <input type="file" name="fichierA">
    <button type="submit">Envoyer</button>
</form>
<p><a href="<%= request.getContextPath() %>/">Retour à l'accueil</a></p>
</body>
</html>
