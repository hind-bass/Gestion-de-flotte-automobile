-- ==============================================
-- REQUÊTES SQL OPTIMISÉES POUR L'APPLICATION
-- Gestion Carburant - Région Oujda, Maroc
-- ==============================================

USE gestion_carburant;

-- ==============================================
-- 1. REQUÊTES POUR DASHBOARD CONTROLLER
-- ==============================================

-- KPIs généraux (pour DashboardController.chargerKPIs())
SELECT 
    COUNT(*) as nombre_ravitaillements,
    SUM(montant_total) as cout_total,
    SUM(quantite) as total_litres,
    AVG(montant_total / quantite) as prix_moyen
FROM Ravitaillement;

-- Consommation moyenne réelle par véhicule (L/100km)
SELECT 
    v.matricule,
    v.marque,
    v.modele,
    AVG((r2.quantite * 100.0) / (r2.kilometrage_releve - r1.kilometrage_releve)) as conso_moyenne_l_100km
FROM Ravitaillement r1
JOIN Ravitaillement r2 ON r1.vehicule_matricule = r2.vehicule_matricule 
    AND r2.date_ravitaillement > r1.date_ravitaillement
JOIN Vehicule v ON r1.vehicule_matricule = v.matricule
WHERE r2.kilometrage_releve > r1.kilometrage_releve
GROUP BY v.matricule, v.marque, v.modele
ORDER BY conso_moyenne_l_100km;

-- ==============================================
-- 2. REQUÊTES POUR RAPPORTS CONTROLLER
-- ==============================================

-- Statistiques par période (3, 6, 12 mois)
-- Pour RapportsController.chargerGraphiques()

-- 3 derniers mois
SELECT 
    DATE_FORMAT(date_ravitaillement, '%Y-%m') as mois,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    COUNT(*) as nombre_ravitaillements,
    AVG(prix_unitaire) as prix_moyen
FROM Ravitaillement 
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
GROUP BY DATE_FORMAT(date_ravitaillement, '%Y-%m')
ORDER BY mois;

-- 6 derniers mois
SELECT 
    DATE_FORMAT(date_ravitaillement, '%Y-%m') as mois,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    COUNT(*) as nombre_ravitaillements,
    AVG(prix_unitaire) as prix_moyen
FROM Ravitaillement 
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
GROUP BY DATE_FORMAT(date_ravitaillement, '%Y-%m')
ORDER BY mois;

-- 12 derniers mois
SELECT 
    DATE_FORMAT(date_ravitaillement, '%Y-%m') as mois,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    COUNT(*) as nombre_ravitaillements,
    AVG(prix_unitaire) as prix_moyen
FROM Ravitaillement 
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
GROUP BY DATE_FORMAT(date_ravitaillement, '%Y-%m')
ORDER BY mois;

-- Répartition par véhicule
SELECT 
    v.matricule,
    v.marque,
    v.modele,
    SUM(r.quantite) as total_litres,
    SUM(r.montant_total) as total_montant,
    COUNT(*) as nombre_ravitaillements,
    AVG(r.quantite) as moyenne_litres_par_ravitaillement
FROM Ravitaillement r
JOIN Vehicule v ON r.vehicule_matricule = v.matricule
GROUP BY v.matricule, v.marque, v.modele
ORDER BY total_montant DESC;

-- Consommation réelle par véhicule (pour RapportsController.chargerGraphiqueConsommation())
SELECT 
    v.matricule,
    v.marque,
    v.modele,
    AVG((r2.quantite * 100.0) / (r2.kilometrage_releve - r1.kilometrage_releve)) as conso_moyenne_l_100km,
    COUNT(*) as nombre_calculs
FROM Ravitaillement r1
JOIN Ravitaillement r2 ON r1.vehicule_matricule = r2.vehicule_matricule 
    AND r2.date_ravitaillement > r1.date_ravitaillement
JOIN Vehicule v ON r1.vehicule_matricule = v.matricule
WHERE r2.kilometrage_releve > r1.kilometrage_releve
GROUP BY v.matricule, v.marque, v.modele
HAVING nombre_calculs >= 1
ORDER BY conso_moyenne_l_100km;

-- ==============================================
-- 3. REQUÊTES POUR IMPRESSION CONTROLLER
-- ==============================================

-- Ravitaillements par période (pour ImpressionController.filtrerRavitaillements())
SELECT 
    r.id_ravitaillement,
    r.date_ravitaillement,
    r.lieu,
    r.quantite,
    r.prix_unitaire,
    r.montant_total,
    r.kilometrage_releve,
    r.conducteur,
    v.matricule as matricule_vehicule,
    v.marque,
    v.modele,
    c.nom_carburant
FROM Ravitaillement r
LEFT JOIN Vehicule v ON r.vehicule_matricule = v.matricule
LEFT JOIN Carburant c ON v.id_carburant = c.id_carburant
WHERE r.date_ravitaillement BETWEEN ? AND ?
ORDER BY r.date_ravitaillement DESC;

-- Statistiques pour période sélectionnée (pour ImpressionController.mettreAJourStatistiques())
SELECT 
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(montant_total / quantite) as prix_moyen,
    MIN(date_ravitaillement) as date_debut,
    MAX(date_ravitaillement) as date_fin
FROM Ravitaillement 
WHERE date_ravitaillement BETWEEN ? AND ?;

-- ==============================================
-- 4. REQUÊTES POUR RAPPORT SERVICE (PDF)
-- ==============================================

-- Données complètes pour rapport PDF (pour RapportService.genererRapportPDF())
SELECT 
    r.id_ravitaillement,
    r.date_ravitaillement,
    r.lieu,
    r.quantite,
    r.prix_unitaire,
    r.montant_total,
    r.kilometrage_releve,
    r.conducteur,
    v.matricule as matricule_vehicule,
    v.marque,
    v.modele,
    c.nom_carburant,
    u.nom as nom_utilisateur,
    cc.numero_carte
FROM Ravitaillement r
LEFT JOIN Vehicule v ON r.vehicule_matricule = v.matricule
LEFT JOIN Carburant c ON v.id_carburant = c.id_carburant
LEFT JOIN Utilisateur u ON r.id_utilisateur = u.id_utilisateur
LEFT JOIN CarteCarburant cc ON r.id_carte = cc.id_carte
ORDER BY r.date_ravitaillement DESC;

-- ==============================================
-- 5. REQUÊTES SPÉCIALISÉES POUR STATISTIQUES AVANCÉES
-- ==============================================

-- Évolution des prix par type de carburant
SELECT 
    c.nom_carburant,
    DATE_FORMAT(p.date_prix, '%Y-%m') as mois,
    p.prix,
    LAG(p.prix) OVER (PARTITION BY c.id_carburant ORDER BY p.date_prix) as prix_precedent,
    ROUND(((p.prix - LAG(p.prix) OVER (PARTITION BY c.id_carburant ORDER BY p.date_prix)) / 
           LAG(p.prix) OVER (PARTITION BY c.id_carburant ORDER BY p.date_prix)) * 100, 2) as evolution_pourcentage
FROM PrixCarburant p
JOIN Carburant c ON p.id_carburant = c.id_carburant
WHERE p.date_prix >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
ORDER BY c.nom_carburant, p.date_prix;

-- Top 5 des stations les plus utilisées
SELECT 
    lieu,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(prix_unitaire) as prix_moyen
FROM Ravitaillement
GROUP BY lieu
ORDER BY nombre_ravitaillements DESC
LIMIT 5;

-- Consommation par conducteur
SELECT 
    conducteur,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(quantite) as moyenne_litres_par_ravitaillement
FROM Ravitaillement
WHERE conducteur IS NOT NULL AND conducteur != ''
GROUP BY conducteur
ORDER BY total_montant DESC;

-- Analyse saisonnière
SELECT 
    CASE 
        WHEN MONTH(date_ravitaillement) IN (12, 1, 2) THEN 'Hiver'
        WHEN MONTH(date_ravitaillement) IN (3, 4, 5) THEN 'Printemps'
        WHEN MONTH(date_ravitaillement) IN (6, 7, 8) THEN 'Été'
        ELSE 'Automne'
    END as saison,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(quantite) as moyenne_litres_par_ravitaillement,
    AVG(prix_unitaire) as prix_moyen
FROM Ravitaillement
GROUP BY saison
ORDER BY 
    CASE saison
        WHEN 'Hiver' THEN 1
        WHEN 'Printemps' THEN 2
        WHEN 'Été' THEN 3
        WHEN 'Automne' THEN 4
    END;

-- ==============================================
-- 6. REQUÊTES POUR COMPARAISONS TEMPORELLES
-- ==============================================

-- Comparaison 3 mois vs 6 mois vs 12 mois
SELECT 
    '3 derniers mois' as periode,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(prix_unitaire) as prix_moyen,
    MIN(date_ravitaillement) as date_debut,
    MAX(date_ravitaillement) as date_fin
FROM Ravitaillement 
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)

UNION ALL

SELECT 
    '6 derniers mois' as periode,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(prix_unitaire) as prix_moyen,
    MIN(date_ravitaillement) as date_debut,
    MAX(date_ravitaillement) as date_fin
FROM Ravitaillement 
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)

UNION ALL

SELECT 
    '12 derniers mois' as periode,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(prix_unitaire) as prix_moyen,
    MIN(date_ravitaillement) as date_debut,
    MAX(date_ravitaillement) as date_fin
FROM Ravitaillement 
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH);

-- ==============================================
-- 7. REQUÊTES POUR ALERTES ET RECOMMANDATIONS
-- ==============================================

-- Véhicules avec consommation anormale (supérieure à 20% de la consommation théorique)
SELECT 
    v.matricule,
    v.marque,
    v.modele,
    v.conso_theorique,
    AVG((r2.quantite * 100.0) / (r2.kilometrage_releve - r1.kilometrage_releve)) as conso_reelle,
    ROUND(((AVG((r2.quantite * 100.0) / (r2.kilometrage_releve - r1.kilometrage_releve)) - v.conso_theorique) / v.conso_theorique) * 100, 2) as ecart_pourcentage
FROM Ravitaillement r1
JOIN Ravitaillement r2 ON r1.vehicule_matricule = r2.vehicule_matricule 
    AND r2.date_ravitaillement > r1.date_ravitaillement
JOIN Vehicule v ON r1.vehicule_matricule = v.matricule
WHERE r2.kilometrage_releve > r1.kilometrage_releve
GROUP BY v.matricule, v.marque, v.modele, v.conso_theorique
HAVING ecart_pourcentage > 20
ORDER BY ecart_pourcentage DESC;

-- Cartes carburant proches du plafond (plus de 80% utilisé)
SELECT 
    cc.numero_carte,
    cc.titulaire,
    cc.plafond,
    SUM(r.montant_total) as montant_utilise,
    ROUND((SUM(r.montant_total) / cc.plafond) * 100, 2) as pourcentage_utilise,
    cc.plafond - SUM(r.montant_total) as montant_restant
FROM CarteCarburant cc
LEFT JOIN Ravitaillement r ON cc.id_carte = r.id_carte
WHERE cc.statut = 'active'
GROUP BY cc.id_carte, cc.numero_carte, cc.titulaire, cc.plafond
HAVING pourcentage_utilise > 80
ORDER BY pourcentage_utilise DESC;

-- ==============================================
-- 8. REQUÊTES POUR EXPORT PDF AVANCÉ
-- ==============================================

-- Rapport détaillé avec toutes les informations
SELECT 
    r.id_ravitaillement,
    r.date_ravitaillement,
    r.lieu,
    r.quantite,
    r.prix_unitaire,
    r.montant_total,
    r.kilometrage_releve,
    r.conducteur,
    v.matricule as matricule_vehicule,
    v.marque,
    v.modele,
    v.conso_theorique,
    c.nom_carburant,
    u.nom as nom_utilisateur,
    cc.numero_carte,
    cc.titulaire as titulaire_carte,
    cc.plafond as plafond_carte
FROM Ravitaillement r
LEFT JOIN Vehicule v ON r.vehicule_matricule = v.matricule
LEFT JOIN Carburant c ON v.id_carburant = c.id_carburant
LEFT JOIN Utilisateur u ON r.id_utilisateur = u.id_utilisateur
LEFT JOIN CarteCarburant cc ON r.id_carte = cc.id_carte
WHERE r.date_ravitaillement BETWEEN ? AND ?
ORDER BY r.date_ravitaillement DESC;

-- Résumé exécutif pour PDF
SELECT 
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(prix_unitaire) as prix_moyen,
    COUNT(DISTINCT vehicule_matricule) as nombre_vehicules,
    COUNT(DISTINCT conducteur) as nombre_conducteurs,
    COUNT(DISTINCT lieu) as nombre_stations,
    MIN(date_ravitaillement) as periode_debut,
    MAX(date_ravitaillement) as periode_fin
FROM Ravitaillement
WHERE date_ravitaillement BETWEEN ? AND ?;

-- ==============================================
-- 9. VUES POUR SIMPLIFIER LES REQUÊTES
-- ==============================================

-- Vue pour les statistiques rapides
CREATE OR REPLACE VIEW v_statistiques_rapides AS
SELECT 
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(prix_unitaire) as prix_moyen,
    MIN(date_ravitaillement) as premier_ravitaillement,
    MAX(date_ravitaillement) as dernier_ravitaillement
FROM Ravitaillement;

-- Vue pour les ravitaillements avec informations complètes
CREATE OR REPLACE VIEW v_ravitaillements_complets AS
SELECT 
    r.*,
    v.marque,
    v.modele,
    v.conso_theorique,
    c.nom_carburant,
    u.nom as nom_utilisateur,
    cc.numero_carte,
    cc.titulaire as titulaire_carte
FROM Ravitaillement r
LEFT JOIN Vehicule v ON r.vehicule_matricule = v.matricule
LEFT JOIN Carburant c ON v.id_carburant = c.id_carburant
LEFT JOIN Utilisateur u ON r.id_utilisateur = u.id_utilisateur
LEFT JOIN CarteCarburant cc ON r.id_carte = cc.id_carte;

-- ==============================================
-- 10. INDEX POUR OPTIMISER LES PERFORMANCES
-- ==============================================

-- Index sur les colonnes les plus utilisées
CREATE INDEX idx_ravitaillement_date ON Ravitaillement(date_ravitaillement);
CREATE INDEX idx_ravitaillement_vehicule ON Ravitaillement(vehicule_matricule);
CREATE INDEX idx_ravitaillement_conducteur ON Ravitaillement(conducteur);
CREATE INDEX idx_ravitaillement_lieu ON Ravitaillement(lieu);
CREATE INDEX idx_prix_carburant_date ON PrixCarburant(date_prix);

-- ==============================================
-- 11. EXEMPLES D'UTILISATION DANS LES SERVICES
-- ==============================================

/*
EXEMPLE D'UTILISATION DANS RavitaillementService.java :

public List<Ravitaillement> getByPeriode(LocalDateTime debut, LocalDateTime fin) {
    String sql = "SELECT * FROM v_ravitaillements_complets " +
                 "WHERE date_ravitaillement BETWEEN ? AND ? " +
                 "ORDER BY date_ravitaillement DESC";
    // Implémentation...
}

public Map<String, Object> getStatistiquesPeriode(LocalDateTime debut, LocalDateTime fin) {
    String sql = "SELECT * FROM v_statistiques_rapides " +
                 "WHERE date_ravitaillement BETWEEN ? AND ?";
    // Implémentation...
}

EXEMPLE D'UTILISATION DANS RapportService.java :

public void genererRapportPDFComplet(LocalDateTime debut, LocalDateTime fin, String chemin) {
    // Utiliser la requête "Rapport détaillé avec toutes les informations"
    // pour générer un PDF complet avec graphiques
}

EXEMPLE D'UTILISATION DANS DashboardController.java :

public void chargerKPIs() {
    // Utiliser la requête "KPIs généraux"
    // Utiliser la requête "Consommation moyenne réelle par véhicule"
}
*/
