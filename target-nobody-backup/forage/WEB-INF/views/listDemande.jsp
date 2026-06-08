<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Liste des demandes</title>
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
            margin-top: 16px;
        }

        th, td {
            border: 1px solid #d1d5db;
            padding: 10px;
            text-align: left;
        }

        th {
            background: #f3f4f6;
        }

        .clickable-row {
            cursor: pointer;
        }

        .clickable-row a {
            color: inherit;
            font-weight: bold;
            text-decoration: none;
        }

        .alert-list {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .alert-item {
            border-left: 5px solid #9ca3af;
            border-radius: 4px;
            padding: 7px 9px;
        }

        .alert-label {
            display: block;
            font-weight: bold;
        }

        .no-alert {
            color: #6b7280;
        }
    </style>
</head>
<body>
<%@ include file="includes/header.jsp" %>
<h1>Liste des demandes</h1>

<div>
    <a href="${pageContext.request.contextPath}/demande/form">+ Creer une demande</a>
    <a href="${pageContext.request.contextPath}/devisDemande/list">Voir les devis</a>
</div>

<table border="1" cellpadding="10" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Client</th>
            <th>Commune ID</th>
            <th>Adresse</th>
            <th>Alerte retard</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="demande" items="${demandes}">
            <tr class="clickable-row"  onclick="window.location='${pageContext.request.contextPath}/demandeStatus/demande?demandeId=${demande.id}'">
                <td>
                    <a href="${pageContext.request.contextPath}/demandeStatus/demande?demandeId=${demande.id}" onclick="event.stopPropagation();">
                        ${demande.id}
                    </a>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${demande.clientDetails != null}">${demande.clientDetails.nom}</c:when>
                        <c:otherwise>${demande.clientId}</c:otherwise>
                    </c:choose>
                </td>
                <td>${demande.communeId}</td>
                <td>${demande.adresse}</td>
                <td>
                    <c:set var="alertes" value="${alertesDemandes[demande.id]}" />
                    <c:choose>
                        <c:when test="${empty alertes}">
                            <span class="no-alert">Aucune alerte configurée pour le statut actuel.</span>
                        </c:when>
                        <c:otherwise>
                            <div class="alert-list">
                                <c:forEach var="alerte" items="${alertes}">
                                    <div class="alert-item" style="border-left-color: ${alerte.couleur};">
                                        <span class="alert-label"><c:out value="${alerte.libelle}" /></span>
                                        <c:choose>
                                            <c:when test="${alerte.retard}">
                                                Alerte : délai dépassé de ${alerte.difference} minute(s)
                                            </c:when>
                                            <c:otherwise>
                                                Délai terminé dans ${-alerte.difference} minute(s)
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/devisDemande">Creer devis</a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<c:if test="${empty demandes}">
    <p>Aucune demande trouvee.</p>
</c:if>

</body>
</html>
