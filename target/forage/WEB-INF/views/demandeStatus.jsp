<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Creation Demande Status</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #f5f5f5; }
        .badge { padding: 4px 8px; border-radius: 999px; font-size: 12px; background: #fff; border: 1px solid #ccc; }
    </style>
</head>
<body>
<h1>Formulaire Demande Status</h1>

<c:if test="${not empty message}">
    <p style="color: #b00020;">${message}</p>
</c:if>

<form action="${pageContext.request.contextPath}/demandeStatus/ajout" method="post" id="formulaire">
    <div>
        <label for="demandeId">Demande :</label>
        <select id="demandeId" name="demandeId" required>
            <option value="">Selectionnez une demande</option>
            <c:forEach var="demande" items="${demandes}">
                <option value="${demande.id}" ${demandeId == demande.id ? 'selected' : ''}>Demande #${demande.id}</option>
            </c:forEach>
        </select>
    </div>
    <div>
        <label for="dateChangement">date :</label>

            <input type="datetime-local" id="dateChangement" name="dateChangement" value="${date}" required />

    </div>
    <div>
        <label for="statusId">Statut :</label>
        <select id="statusId" name="statusId" required>
            <option value="">Selectionnez un statut</option>
            <c:forEach var="status" items="${status}">
                <option value="${status.id}">${status.libelle}</option>
            </c:forEach>
        </select>
    </div>
    <p id="forageInfo" style="color: #b00020; display: none;">Le type Forage est deja utilise pour cette demande.</p>
    <div>
        <label for="observation">Observation :</label>
        <input type="text" id="observation" name="observation" required />
    </div>

    <div>
        <button type="submit">Enregistrer</button>
        <a href="${pageContext.request.contextPath}/demandeStatus/ajout">Annuler</a>
    </div>
</form>

<form action="${pageContext.request.contextPath}/devisDemande/details" method="post" style="margin-top: 12px;" id="detailsForm">
    <input type="hidden" id="demandeIdDetails" name="demandeId" value="${demandeId}" />
    <button type="submit">Voir details devis</button>
</form>

<h2>Tableau de bord des demandes</h2>
<form action="${pageContext.request.contextPath}/demandeStatus/dashboard" method="get">
    <label for="maintenant">Date de référence :</label>
    <input type="datetime-local" id="maintenant" name="maintenant" />
    <button type="submit">Actualiser</button>
</form>

<table>
    <thead>
        <tr>
            <th>ID Demande</th>
            <th>Statut actuel</th>
            <th>Date dernier changement</th>
            <th>Durée (heures)</th>
            <th>Seuil minimum (heures)</th>
            <th>Message</th>
            <th>Couleur</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="row" items="${rows}">
            <tr style="background-color: ${row.couleur};">
                <td>${row.demandeId}</td>
                <td><span class="badge">${row.statutActuel}</span></td>
                <td>${row.dateDernierChangement}</td>
                <td>${row.dureeHeures}</td>
                <td>${row.seuilHeures}</td>
                <td>${row.message}</td>
                <td>${row.couleur}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>
