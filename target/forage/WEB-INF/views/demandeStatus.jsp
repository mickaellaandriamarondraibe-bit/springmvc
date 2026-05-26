<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Creation Demande Status</title>
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

</body>
</html>
