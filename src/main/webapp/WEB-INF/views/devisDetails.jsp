<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Details des devis</title>
</head>
<body>
<h1>Details des devis</h1>

<c:if test="${not empty message}">
    <p style="color: #b00020;">${message}</p>
</c:if>

<c:if test="${not empty devisList}">
    <form action="${pageContext.request.contextPath}/exporterPDF" method="post">
    <table border="1" cellpadding="10" cellspacing="0">
        <thead>
            <tr>
                <th>ID Devis</th>
                <th>Observation</th>
                <th>Date creation</th>
                <th>Type</th>
                <th>Details</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="devis" items="${devisList}">
                <tr>
                    <td>${devis.id}</td>
                    <td>${devis.observation}</td>
                    <td>${devis.dateCreation}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty devis.type}">
                                ${devis.type.libelle}
                            </c:when>
                            <c:otherwise>
                                N/A
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:forEach var="detail" items="${detailsByDevis[devis.id]}">
                            <div>${detail.libelle} | qte=${detail.qte} | pu=${detail.prixUnitaire}</div>
                            <input type="hidden" name="libelle" value="${detail.libelle}" />
                            <input type="hidden" name="qte" value="${detail.qte}" />
                            <input type="hidden" name="prixUnitaire" value="${detail.prixUnitaire}" />
                        </c:forEach>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <button type="submit" style="margin-top: 10px;">Exporter en PDF</button>
    </form>
</c:if>

<c:if test="${empty devisList and empty message}">
    <p>Aucun devis trouve pour cette demande.</p>
</c:if>

<div style="margin-top: 12px;">
    <a href="${pageContext.request.contextPath}/demande/list">Retour liste demandes</a>
</div>
</body>
</html>
