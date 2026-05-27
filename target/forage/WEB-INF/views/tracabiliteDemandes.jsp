<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Tracabilite Des Demandes</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 16px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #f5f5f5; }
        .badge { padding: 4px 8px; border-radius: 999px; font-size: 12px; background: #fff; border: 1px solid #ccc; }
        .actions { margin-bottom: 12px; }
    </style>
</head>
<body>
<h1>Liste De Tracabilite Des Demandes</h1>

<div class="actions">
    <a href="${pageContext.request.contextPath}/demandeStatus">Retour formulaire statut</a>
</div>

<table>
    <thead>
        <tr>
            <th>ID Trace</th>
            <th>ID Demande</th>
            <th>Statut</th>
            <th>Date changement</th>
            <th>Observation</th>
            <th>Duree travail (min)</th>
            <th>Couleur</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="trace" items="${demandeStatus}">
            <c:set var="lineColor" value="${statusColors[trace.status.id]}" />
            <tr style="background-color: ${empty lineColor ? '#F3F4F6' : lineColor};">
                <td>${trace.id}</td>
                <td>${trace.demande.id}</td>
                <td><span class="badge">${trace.status.libelle}</span></td>
                <td>${trace.dateChangement}</td>
                <td>${trace.observation}</td>
                <td>${trace.dureeTravail}</td>
                <td>${empty lineColor ? '#F3F4F6' : lineColor}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>
