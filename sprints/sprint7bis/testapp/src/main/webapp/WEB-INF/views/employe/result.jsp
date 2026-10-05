<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Résultat</title>
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
<%!
    /** Échappe le texte saisi ; null et chaîne vide sont rendus visibles. */
    static String h(Object o) {
        if (o == null || "null".equals(o.toString())) return "<span class=\"nul\">null</span>";
        if (o.toString().isEmpty()) return "<span class=\"nul\">\"\" (vide)</span>";
        return o.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<h1>Ce que la méthode a reçu</h1>
<p class="sub">Méthode appelée : <code><%= request.getAttribute("methode") == null ? "" : request.getAttribute("methode").toString().replace("<", "&lt;") %></code></p>
<div class="carte">
  <table>
    <tr><th>Argument</th><th>Valeur</th></tr>
    <tr><td>nom</td><td><%= h(request.getAttribute("nom")) %></td></tr>
    <% if (request.getAttribute("prenom") != null) { %><tr><td>prenom</td><td><%= h(request.getAttribute("prenom")) %></td></tr><% } %>
    <% if (request.getAttribute("age") != null) { %><tr><td>age</td><td><%= h(request.getAttribute("age")) %></td></tr><% } %>
  </table>
</div>
<p><a href="<%= request.getContextPath() %>/employe/form">Retour au formulaire</a></p>
</body>
</html>
