<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Detail demande</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; max-width: 900px; margin-top: 16px; }
        th, td { border-bottom: 1px solid #ddd; padding: 14px 8px; text-align: left; }
        th { background: #f5f5f5; }
        .badge { padding: 4px 8px; border-radius: 999px; font-size: 12px; background: #fff; border: 1px solid #ccc; }
        .current-alert { border-left: 5px solid #9ca3af; margin-bottom: 16px; padding: 10px; }
        .calcul { background: #e5e7eb; border-radius: 5px; padding: 2px 7px; font-family: monospace; }
        .simulation { background: #f5f5f5; max-width: 650px; margin-bottom: 16px; padding: 12px; }
        .simulation form { display: flex; align-items: end; gap: 8px; flex-wrap: wrap; }
        .simulation label { display: flex; flex-direction: column; gap: 4px; }
        .erreur { color: #b91c1c; }
    </style>
</head>
<body>
<%@ include file="includes/header.jsp" %>
<h1>Historique de la demande #${demande.id}</h1>

<p><a href="${pageContext.request.contextPath}/demande/list">Retour liste demandes</a></p>

<c:if test="${not empty message}">
    <p class="erreur">${message}</p>
</c:if>

<div class="simulation">
    <form action="${pageContext.request.contextPath}/demandeStatus/demande" method="get">
        <input type="hidden" name="demandeId" value="${demande.id}" />
        <label>
            Date et heure de simulation
            <input type="datetime-local" name="dateSimulation" value="${dateSimulation}" required />
        </label>
        <button type="submit">Simuler</button>
        <a href="${pageContext.request.contextPath}/demandeStatus/demande?demandeId=${demande.id}">Utiliser l'heure actuelle</a>
    </form>
    <c:if test="${not empty dateSimulation}">
        <p>Simulation active au <strong class="date-saisie" data-date="${dateCalcul}">${dateCalcul}</strong>.</p>
    </c:if>
    <c:if test="${not empty erreurSimulation}">
        <p class="erreur">${erreurSimulation}</p>
    </c:if>
</div>

<c:if test="${not empty alerteActuelle}">
    <div class="current-alert" style="border-left-color: ${alerteActuelle.retard ? alerteActuelle.couleur : '#F3F4F6'};">
        <strong>Statut actuel : <c:out value="${alerteActuelle.libelle}" /></strong><br />
        <c:choose>
            <c:when test="${alerteActuelle.retard}">
                Alerte : délai dépassé de ${alerteActuelle.difference} minute(s)
            </c:when>
            <c:otherwise>
                Délai restant : ${-alerteActuelle.difference} minute(s) ouvrée(s)
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

<h2>Statuts</h2>
<table>
    <thead>
        <tr>
            <th>Statut</th>
            <th>Date saisie</th>
            <th>DT calculé</th>
            <th>Résultat</th>
            <th>Modifier la date</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="ligne" items="${resultatsHistorique}">
            <tr style="border-left: 5px solid ${ligne.retard ? ligne.couleur : 'transparent'};">
                <td>${ligne.trace.status.libelle}</td>
                <td class="date-saisie" data-date="${ligne.trace.dateChangement}">${ligne.trace.dateChangement}</td>
                <td>
                    <c:choose>
                        <c:when test="${ligne.enCours}">En cours</c:when>
                        <c:otherwise>${ligne.dureeTravail} min</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${ligne.enCours}">
                            En cours
                        </c:when>
                        <c:when test="${empty ligne.limite}">
                            ${ligne.dureeTravail == 0 ? 'Aucun retard' : 'Aucun délai configuré'}
                        </c:when>
                        <c:when test="${ligne.retard}">
                            Alerte :
                            <span class="calcul">${ligne.dureeTravail} - ${ligne.limite} = ${ligne.difference} min</span>
                            de retard
                        </c:when>
                        <c:otherwise>
                            Pas d'alerte :
                            <span class="calcul">${ligne.dureeTravail} ≤ ${ligne.limite}</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <form action="${pageContext.request.contextPath}/demandeStatus/modifier-date" method="post">
                        <input type="hidden" name="demandeStatusId" value="${ligne.trace.id}" />
                        <input type="hidden" name="demandeId" value="${demande.id}" />
                        <input type="datetime-local" name="nouvelleDate" value="${ligne.trace.dateChangement}" step="1" required />
                        <button type="submit">Modifier</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<script>
    document.querySelectorAll(".date-saisie").forEach(cellule => {
        const date = new Date(cellule.dataset.date);
        const deuxChiffres = valeur => String(valeur).padStart(2, "0");
        cellule.textContent = deuxChiffres(date.getDate()) + "/"
            + deuxChiffres(date.getMonth() + 1) + "/"
            + date.getFullYear() + " "
            + deuxChiffres(date.getHours()) + "h"
            + deuxChiffres(date.getMinutes());
    });
</script>

</body>
</html>
