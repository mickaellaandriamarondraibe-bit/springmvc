<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Liste des demandes</title>
</head>
<body>
<h1>Liste des demandes</h1>

<div>
    <a href="${pageContext.request.contextPath}/demande/form">+ Creer une demande</a>
</div>

<table border="1" cellpadding="10" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Client</th>
            <th>Commune ID</th>
            <th>Adresse</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="demande" items="${demandes}">
            <tr>
                <td>${demande.id}</td>
                <td>
                    <c:choose>
                        <c:when test="${demande.clientDetails != null}">${demande.clientDetails.nom}</c:when>
                        <c:otherwise>${demande.clientId}</c:otherwise>
                    </c:choose>
                </td>
                <td>${demande.communeId}</td>
                <td>${demande.adresse}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/devisDemande">Creer devis</a>
                </td>
            </tr>
        </c:forEach>
        <a href="${pageContext.request.contextPath}/devisDemande/list">Voir les devis</a>
    </tbody>
</table>

<c:if test="${empty demandes}">
    <p>Aucune demande trouvee.</p>
</c:if>

</body>
</html>
