<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Object sessionId = request.getAttribute("sessionId");
    Object employe = request.getAttribute("employe");
%>
<p>ID session : <strong><%= sessionId %></strong></p>

<% if (employe instanceof com.app.model.Employe) {
    com.app.model.Employe e = (com.app.model.Employe) employe;
%>
    <p>Employé : <%= e.getNom() %> <%= e.getPrenom() %> <% if(e.getAge()!=null){ %>- <%= e.getAge() %> ans<% } %> <% if(e.getMatricule()!=null && !e.getMatricule().isEmpty()){ %>- <%= e.getMatricule() %><% } %></p>
<% } else { %>
    <p>Aucun employé enregistré dans cette session</p>
<% } %>
