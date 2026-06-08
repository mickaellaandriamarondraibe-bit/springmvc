<style>
    .app-header {
        background: #1f2937;
        border-radius: 6px;
        margin-bottom: 24px;
        padding: 12px 16px;
    }

    .app-header__brand {
        color: #ffffff;
        display: inline-block;
        font-weight: bold;
        margin-right: 24px;
        text-decoration: none;
    }

    .app-header__nav {
        display: inline-flex;
        flex-wrap: wrap;
        gap: 10px;
        vertical-align: middle;
    }

    .app-header__nav a {
        color: #e5e7eb;
        padding: 6px 8px;
        text-decoration: none;
    }

    .app-header__nav a:hover {
        background: #374151;
        border-radius: 4px;
        color: #ffffff;
    }
</style>

<header class="app-header">
    <a class="app-header__brand" href="${pageContext.request.contextPath}/home">Forage</a>
    <nav class="app-header__nav" aria-label="Navigation principale">
        <a href="${pageContext.request.contextPath}/demande/form">Nouvelle demande</a>
        <a href="${pageContext.request.contextPath}/demande/list">Demandes</a>
        <a href="${pageContext.request.contextPath}/devisDemande">Nouveau devis</a>
        <a href="${pageContext.request.contextPath}/devisDemande/list">Devis</a>
        <a href="${pageContext.request.contextPath}/demandeStatus">Statuts</a>
        <a href="${pageContext.request.contextPath}/demandeStatus/tracabilite">Tracabilite</a>
    </nav>
</header>
