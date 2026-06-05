<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Detail demande</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 16px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #f5f5f5; }
        .badge { padding: 4px 8px; border-radius: 999px; font-size: 12px; background: #fff; border: 1px solid #ccc; }
        .current-alert { border-left: 5px solid #9ca3af; margin-bottom: 16px; padding: 10px; }
    </style>
</head>
<body>
<%@ include file="includes/header.jsp" %>
<h1>Historique de la demande #${demande.id}</h1>

<p><a href="${pageContext.request.contextPath}/demande/list">Retour liste demandes</a></p>
<c:if test="${not empty alerteActuelle}">
    <div class="current-alert" style="border-left-color: ${alerteActuelle.couleur};">
        <strong>Statut actuel : <c:out value="${alerteActuelle.libelle}" /></strong><br />
        <c:out value="${alerteActuelle.message}" />
    </div>
</c:if>

<h2>Statuts</h2>
<table>
    <thead>
        <tr>
            <th>ID Trace</th>
            <th>Statut</th>
            <th>Date changement</th>
            <th>Observation</th>
            <th>Duree travail (min)</th>
            <th>État du délai</th>
            <th>Modifier date</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="trace" items="${demandeStatus}">
            <c:set var="estActuel" value="${trace.id == dernierStatusId}" />
            <tr style="background-color: ${estActuel && not empty alerteActuelle && alerteActuelle.retard ? alerteActuelle.couleur : '#F3F4F6'};">
                <td>${trace.id}</td>
                <td><span class="badge">${trace.status.libelle}</span></td>
                <td>${trace.dateChangement}</td>
                <td>${trace.observation}</td>
                <td>${trace.dureeTravail}</td>
                <td>
                    <c:if test="${estActuel && not empty alerteActuelle}">
                        <c:out value="${alerteActuelle.message}" />
                    </c:if>
                </td>
                <td>
                    <form action="${pageContext.request.contextPath}/demandeStatus/modifier-date" method="post">
                        <input type="hidden" name="demandeStatusId" value="${trace.id}" />
                        <input type="datetime-local" name="nouvelleDate" value="${trace.dateChangement}" step="1" required />
                        <button type="submit">Modifier</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>
