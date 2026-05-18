<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Liste des devis</title>
</head>
<body>
<h1>Liste des devis</h1>

<table border="1" cellpadding="10" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Observation</th>
            <th>Date creation</th>
            <th>Type</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="devis" items="${devisList}">
            <tr>
                <td>${devis.id}</td>
                <td>${devis.observation}</td>
                <td>${devis.dateCreation}</td>
                <td>${devis.demande.id}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/devis/accepter" method="post" style="display:inline;">
                        <input type="hidden" name="demandeId" value="${devis.demande.id}" />
                        <button type="submit">Accepter</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/devis/refuser" method="post" style="display:inline;">
                        <input type="hidden" name="demandeId" value="${devis.demande.id}" />
                        <button type="submit">Refuser</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<c:if test="${empty devisList}">
    <p>Aucun devis trouve.</p>
</c:if>

</body>
</html>
