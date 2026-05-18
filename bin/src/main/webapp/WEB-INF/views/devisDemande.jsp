<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Creation Devis</title>
</head>
<body>
<h1>Formulaire Devis</h1>

<c:if test="${not empty message}">
    <p style="color: #b00020;">${message}</p>
</c:if>

<form action="${pageContext.request.contextPath}/devisDemande" method="post" id="formulaire">
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
        <label for="typeId">Type devis :</label>
        <select id="typeId" name="typeId" required>
            <option value="">Selectionnez un type</option>
            <c:forEach var="type" items="${typesDevis}">
                <c:if test="${type.id == 1}">
                    <option value="${type.id}">${type.libelle}</option>
                </c:if>
            </c:forEach>
        </select>
    </div>
    <div>
        <label for="observation">Observation :</label>
        <input type="text" id="observation" name="observation" required />
    </div>

    <title>Creation Details Devis</title>
    <div id="champs-supplementaires"></div>

    <div>
        <button id="bouton" type="button">Ajouter</button>
        <button type="submit">Enregistrer devis</button>
        <a href="${pageContext.request.contextPath}/demande/list">Retour liste demandes</a>
    </div>

</form>

<form action="${pageContext.request.contextPath}/devisDemande/details" method="post" style="margin-top: 12px;">
    <input type="hidden" id="demandeIdDetails" name="demandeId" value="${demandeId}" />
    <button type="submit">Voir details devis</button>
</form>
<script>
const bouton = document.getElementById('bouton');
const container = document.getElementById('champs-supplementaires');
const demandeSelect = document.getElementById('demandeId');
const demandeIdDetails = document.getElementById('demandeIdDetails');
let countChamps = 0;

function ajouterLigne() {
    countChamps += 1;

    const bloc = document.createElement('div');
    bloc.style.marginTop = '8px';

    const labelLibelle = document.createElement('label');
    labelLibelle.textContent = 'Libelle : ';

    const libelle = document.createElement('input');
    libelle.type = 'text';
    libelle.name = 'libelle';
    libelle.id = 'libelle_' + countChamps;
    libelle.required = true;

    const labelQte = document.createElement('label');
    labelQte.textContent = ' Qte : ';

    const qte = document.createElement('input');
    qte.type = 'number';
    qte.name = 'qte';
    qte.id = 'qte_' + countChamps;
    qte.min = '1';
    qte.required = true;

    const labelPrixUnitaire = document.createElement('label');
    labelPrixUnitaire.textContent = ' Prix unitaire : ';

    const prixUnitaire = document.createElement('input');
    prixUnitaire.type = 'number';
    prixUnitaire.name = 'prixUnitaire';
    prixUnitaire.id = 'prixUnitaire_' + countChamps;
    prixUnitaire.step = '0.01';
    prixUnitaire.min = '0';
    prixUnitaire.required = true;

    bloc.appendChild(labelLibelle);
    bloc.appendChild(libelle);
    bloc.appendChild(labelQte);
    bloc.appendChild(qte);
    bloc.appendChild(labelPrixUnitaire);
    bloc.appendChild(prixUnitaire);

    container.appendChild(bloc);
}

bouton.addEventListener('click', ajouterLigne);
demandeSelect.addEventListener('change', function () {
    demandeIdDetails.value = this.value;
});
ajouterLigne();
</script>
</body>
</html>
