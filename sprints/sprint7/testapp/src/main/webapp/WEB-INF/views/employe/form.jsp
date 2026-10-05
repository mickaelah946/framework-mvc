<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Formulaire employé</title>
<style>
  body{font-family:system-ui,Segoe UI,Arial,sans-serif;max-width:720px;margin:2rem auto;padding:0 1rem;color:#222;background:#f6f7f9}
  h1{font-size:1.5rem;margin-bottom:.2rem} .sub{color:#666;margin-top:0}
  .carte{background:#fff;border:1px solid #dde1e6;border-radius:10px;padding:1rem 1.2rem;margin:1rem 0}
  .carte h2{font-size:1rem;margin:0 0 .3rem} code{background:#eef1f5;padding:.1rem .35rem;border-radius:4px;font-size:.85rem}
  label{display:block;margin:.6rem 0 .2rem;font-size:.9rem} input{padding:.45rem .6rem;border:1px solid #bbb;border-radius:6px;width:100%;box-sizing:border-box}
  button{margin-top:.8rem;padding:.5rem 1.1rem;border:0;border-radius:6px;background:#2f6fed;color:#fff;cursor:pointer}
  table{border-collapse:collapse;width:100%} td,th{border-bottom:1px solid #e5e8ec;padding:.5rem;text-align:left} .nul{color:#b00;font-style:italic}
  a{color:#2f6fed}
</style>
</head>
<body>
<h1>Sprint 7 : arguments de méthode</h1>
<p class="sub">Les champs sont reçus par le nom de l'argument.</p>
<div class="carte">
  <h2>POST <code>/employe/save</code></h2>
  <p><code>save(String nom, String prenom, Integer age)</code></p>
  <form method="post" action="<%= request.getContextPath() %>/employe/save">
    <label>Nom</label><input type="text" name="nom">
    <label>Prénom</label><input type="text" name="prenom">
    <label>Âge</label><input type="number" name="age">
    <button type="submit">Valider</button>
  </form>
</div>
<div class="carte">
  <h2>Tests en GET</h2>
  <ul>
    <li><a href="<%= request.getContextPath() %>/employe/hello?nom=Mimi">/employe/hello?nom=Mimi</a></li>
    <li><a href="<%= request.getContextPath() %>/employe/hello">/employe/hello</a> (sans paramètre : null)</li>
    <li><a href="<%= request.getContextPath() %>/employe/json?nom=Mimi&amp;age=30">/employe/json?nom=Mimi&amp;age=30</a> (JSON)</li>
  </ul>
</div>
<p><a href="<%= request.getContextPath() %>/">Retour à l'accueil</a></p>
</body>
</html>
