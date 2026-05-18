<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Creer une demande</title>
</head>
<body>
<h1>Creer une demande</h1>
<p id="ajaxError" style="color: #b00020;"></p>

<form action="${pageContext.request.contextPath}/demande/submit" method="post">
    <div>
        <label for="clientId">Client :</label>
        <select id="clientId" name="clientId" required>
            <option value="">Selectionnez un client</option>
            <c:forEach var="client" items="${idclient}">
                <option value="${client.id}">${client.nom}</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label for="regionId">Region :</label>
        <select id="regionId" required>
            <option value="">Selectionnez une region</option>
            <c:forEach var="region" items="${regions}">
                <option value="${region.id}">${region.libelle}</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label for="districtId">District :</label>
        <select id="districtId" required>
            <option value="">Selectionnez un district</option>
        </select>
    </div>

    <div>
        <label for="communeId">Commune :</label>
        <select id="communeId" name="communeId" required>
            <option value="">Selectionnez une commune</option>
        </select>
    </div>

    <div>
        <label for="adresse">Adresse :</label>
        <input type="text" id="adresse" name="adresse" />
    </div>

    <div>
        <button type="submit">Enregistrer</button>
        <a href="${pageContext.request.contextPath}/demande/list">Annuler</a>
    </div>
</form>

<script>
    const ctx = '${pageContext.request.contextPath}';
    const regionSelect = document.getElementById('regionId');
    const districtSelect = document.getElementById('districtId');
    const communeSelect = document.getElementById('communeId');
    const ajaxError = document.getElementById('ajaxError');

    function resetSelect(select, placeholder) {
        select.innerHTML = '';
        const option = document.createElement('option');
        option.value = '';
        option.textContent = placeholder;
        select.appendChild(option);
    }

    function fillSelect(select, items) {
        items.forEach(item => {
            const option = document.createElement('option');
            option.value = item.id;
            option.textContent = item.libelle;
            select.appendChild(option);
        });
    }

    regionSelect.addEventListener('change', async function () {
        const regionId = this.value;
        ajaxError.textContent = '';
        resetSelect(districtSelect, 'Selectionnez un district');
        resetSelect(communeSelect, 'Selectionnez une commune');
        if (!regionId) return;
        try {
            const response = await fetch(
                ctx + '/demande/districts?regionId=' + encodeURIComponent(regionId)
            );
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }
            const districts = await response.json();
            fillSelect(districtSelect, districts);
        } catch (e) {
            ajaxError.textContent = 'Erreur chargement districts: ' + e.message;
        }
    });

    districtSelect.addEventListener('change', async function () {
        const districtId = this.value;
        ajaxError.textContent = '';
        resetSelect(communeSelect, 'Selectionnez une commune');
        if (!districtId) return;
        try {
            const response = await fetch(
                ctx + '/demande/communes?districtId=' + encodeURIComponent(districtId)
            );
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }
            const communes = await response.json();
            fillSelect(communeSelect, communes);
        } catch (e) {
            ajaxError.textContent = 'Erreur chargement communes: ' + e.message;
        }
    });
</script>
</body>
</html>
