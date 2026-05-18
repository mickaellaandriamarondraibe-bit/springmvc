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
                <option value="${type.id}" data-type-id="${type.id}">${type.libelle}</option>
            </c:forEach>
        </select>
    </div>
    <div>
        <label for="statusId">Statut :</label>
        <select id="statusId" name="statusId" required>
            <option value="">Selectionnez un statut</option>
            <c:forEach var="entry" items="${statusMap}">
                <option value="${entry.key}">${entry.value.libelle}</option>
            </c:forEach>
        </select>
    </div>
    <p id="forageInfo" style="color: #b00020; display: none;">Le type Forage est deja utilise pour cette demande.</p>
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
const typeSelect = document.getElementById('typeId');
const forageInfo = document.getElementById('forageInfo');
const ctx = '${pageContext.request.contextPath}';
let countChamps = 0;

async function refreshTypeOptionsByDemande() {
    const demandeId = demandeSelect.value;
    const forageOption = typeSelect.querySelector('option[data-type-id="2"]');

    if (!demandeId || !forageOption) {
        if (forageOption) {
            forageOption.disabled = false;
            forageOption.hidden = false;
        }
        forageInfo.style.display = 'none';
        return;
    }

    try {
        const response = await fetch(ctx + '/devisDemande/forageLocked?demandeId=' + encodeURIComponent(demandeId));
        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }
        const data = await response.json();
        const locked = !!data.forageLocked;

        forageOption.disabled = locked;
        forageOption.hidden = locked;
        if (locked && typeSelect.value === '2') {
            typeSelect.value = '';
        }
        forageInfo.style.display = locked ? 'block' : 'none';
    } catch (e) {
        forageInfo.style.display = 'none';
    }
}

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
    refreshTypeOptionsByDemande();
});
refreshTypeOptionsByDemande();
ajouterLigne();
</script>
</body>
</html>
