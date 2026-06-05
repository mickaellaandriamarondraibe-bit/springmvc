<?php
$apiUrl = "http://localhost:8080/forage/demandeStatus/api/dashboard";

$ch = curl_init($apiUrl); //Prépare une requête vers cette API.
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); //Je veux modifier une option de ma requête cURL.
curl_setopt($ch, CURLOPT_HTTPHEADER, ["Accept: application/json"]); //Je veux ajouter des headers HTTP à ma requête.
$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curlErr = curl_error($ch);
curl_close($ch);

if ($response === false) {
    die("Erreur cURL: " . htmlspecialchars($curlErr));
}

if ($httpCode !== 200) {
    die("Erreur API dashboard - HTTP " . (int)$httpCode . "<pre>" . htmlspecialchars($response) . "</pre>");
}

$rows = json_decode($response, true);
if (!is_array($rows)) {
    die("Réponse JSON invalide: <pre>" . htmlspecialchars($response) . "</pre>");
}

function formatDateFromArray($d): string
{
    if (!is_array($d) || count($d) < 5) {
        return "";
    }

    $y = (int)$d[0];
    $m = str_pad((string)$d[1], 2, "0", STR_PAD_LEFT);
    $day = str_pad((string)$d[2], 2, "0", STR_PAD_LEFT);
    $h = str_pad((string)$d[3], 2, "0", STR_PAD_LEFT);
    $i = str_pad((string)$d[4], 2, "0", STR_PAD_LEFT);
    $s = isset($d[5]) ? str_pad((string)$d[5], 2, "0", STR_PAD_LEFT) : "00";

    return "$y-$m-$day $h:$i:$s";
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Dashboard</title>
  <style>
    table { border-collapse: collapse; width: 100%; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background: #f5f5f5; }
  </style>
</head>
<body>
  <h2>Tableau de bord des demandes</h2>

  <table>
    <thead>
      <tr>
        <th>ID Demande</th>
        <th>Statut actuel</th>
        <th>Date dernier changement</th>
        <th>Durée (minutes)</th>
        <th>Seuil minimum (minutes)</th>
        <th>Message</th>
        <th>Couleur</th>
      </tr>
    </thead>
    <tbody>
      <?php if (empty($rows)): ?>
        <tr>
          <td colspan="7">Aucune donnée.</td>
        </tr>
      <?php else: ?>
        <?php foreach ($rows as $row): ?>
          <?php $bg = $row['couleur'] ?? '#F3F4F6'; ?>
          <tr style="background-color: <?= htmlspecialchars($bg) ?>;">
            <td><?= htmlspecialchars((string)($row['demandeId'] ?? '')) ?></td>
            <td><?= htmlspecialchars((string)($row['statutActuel'] ?? '')) ?></td>
            <td><?= htmlspecialchars(formatDateFromArray($row['dateDernierChangement'] ?? null)) ?></td>
            <td><?= htmlspecialchars((string)($row['dureeMinutes'] ?? '')) ?></td>
            <td><?= htmlspecialchars((string)($row['seuilMinutes'] ?? '')) ?></td>
            <td><?= htmlspecialchars((string)($row['message'] ?? '')) ?></td>
            <td><?= htmlspecialchars((string)($row['couleur'] ?? '')) ?></td>
          </tr>
        <?php endforeach; ?>
      <?php endif; ?>
    </tbody>
  </table>
</body>
</html>
