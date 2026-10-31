-- ==============================================
-- MISE À JOUR DES LIEUX DE RAVITAILLEMENT EN FRANÇAIS
-- Région d'Oujda, Maroc
-- ==============================================

USE gestion_carburant;

-- ==============================================
-- 1. MISE À JOUR DES LIEUX EXISTANTS
-- ==============================================

-- Mise à jour des lieux en français pour la région d'Oujda
UPDATE Ravitaillement SET lieu = 'Station Total - Centre Oujda' 
WHERE lieu = 'محطة توتال - وجدة المركز';

UPDATE Ravitaillement SET lieu = 'Station Shell - Nouvelle Ville Oujda' 
WHERE lieu = 'محطة شل - وجدة المدينة الجديدة';

UPDATE Ravitaillement SET lieu = 'Station BP - Autoroute Oujda' 
WHERE lieu = 'محطة بي بي - وجدة الطريق السيار';

UPDATE Ravitaillement SET lieu = 'Station Esso - Quartier Al Amal' 
WHERE lieu = 'محطة إيسو - وجدة حي الأمل';

UPDATE Ravitaillement SET lieu = 'Station Total - Quartier An-Nahda' 
WHERE lieu = 'محطة توتال - وجدة حي النهضة';

UPDATE Ravitaillement SET lieu = 'Station Avia - Quartier As-Salam' 
WHERE lieu = 'محطة أفيا - وجدة حي السلام';

UPDATE Ravitaillement SET lieu = 'Station Leclerc - Quartier Ar-Riyad' 
WHERE lieu = 'محطة ليكليرك - وجدة حي الرياض';

UPDATE Ravitaillement SET lieu = 'Station Intermarché - Quartier Lazari' 
WHERE lieu = 'محطة إنترمارشيه - وجدة حي لازاري';

UPDATE Ravitaillement SET lieu = 'Station Carrefour - Quartier Al-Andalus' 
WHERE lieu = 'محطة كارفور - وجدة حي الأندلس';

-- ==============================================
-- 2. AJOUT DE NOUVEAUX LIEUX EN FRANÇAIS
-- ==============================================

-- Ajout de stations supplémentaires pour diversifier les lieux
INSERT INTO Ravitaillement (date_ravitaillement, lieu, quantite, prix_unitaire, montant_total, kilometrage_releve, vehicule_matricule, id_utilisateur, id_carte, conducteur) VALUES 
-- Stations dans le centre-ville
('2024-10-15 10:30:00', 'Station Afriquia - Place du 16 Novembre', 42.0, 12.60, 529.20, 12000, '1234-A', 1, 1, 'Ahmed Benali'),
('2024-11-20 14:45:00', 'Station Petrom - Avenue Hassan II', 38.0, 12.80, 486.40, 25000, '5678-C', 2, 2, 'Fatima Zahra'),
('2024-12-10 16:20:00', 'Station Mobil - Boulevard Mohammed V', 45.0, 13.00, 585.00, 15000, '9101-E', 1, 1, 'Mohamed Alami'),

-- Stations sur les axes routiers principaux
('2025-01-15 09:15:00', 'Station Total - Route de Berkane', 40.0, 13.20, 528.00, 18000, '2345-G', 3, 3, 'Aicha Mansouri'),
('2025-02-20 11:30:00', 'Station Shell - Route de Nador', 43.0, 13.40, 576.20, 22000, '6789-A', 2, 2, 'Ahmed Benali'),
('2025-03-25 15:10:00', 'Station BP - Route de Taourirt', 39.0, 13.60, 530.40, 19000, '1234-A', 4, 4, 'Fatima Zahra'),

-- Stations dans les quartiers résidentiels
('2025-04-10 12:45:00', 'Station Esso - Quartier Al Qods', 41.0, 13.80, 565.80, 16000, '5678-C', 1, 1, 'Mohamed Alami'),
('2025-05-15 17:30:00', 'Station Avia - Quartier Al Fath', 44.0, 14.00, 616.00, 21000, '9101-E', 3, 3, 'Aicha Mansouri'),
('2025-06-20 13:20:00', 'Station Leclerc - Quartier Al Mansour', 37.0, 14.20, 525.40, 17000, '2345-G', 2, 2, 'Ahmed Benali'),

-- Stations près des zones commerciales
('2025-07-05 10:45:00', 'Station Intermarché - Zone Industrielle', 46.0, 14.40, 662.40, 20000, '6789-A', 4, 4, 'Fatima Zahra'),
('2025-08-10 14:15:00', 'Station Carrefour - Centre Commercial', 42.0, 14.60, 613.20, 18000, '1234-A', 1, 1, 'Mohamed Alami'),
('2025-09-01 16:00:00', 'Station Total - Marché Central', 40.0, 14.80, 592.00, 19000, '5678-C', 3, 3, 'Aicha Mansouri');

-- ==============================================
-- 3. VÉRIFICATION DES LIEUX MIS À JOUR
-- ==============================================

-- Afficher tous les lieux uniques en français
SELECT DISTINCT lieu, COUNT(*) as nombre_ravitaillements
FROM Ravitaillement 
GROUP BY lieu 
ORDER BY nombre_ravitaillements DESC;

-- ==============================================
-- 4. STATISTIQUES PAR LIEU EN FRANÇAIS
-- ==============================================

-- Top 10 des stations les plus utilisées
SELECT 
    lieu,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(prix_unitaire) as prix_moyen
FROM Ravitaillement
GROUP BY lieu
ORDER BY nombre_ravitaillements DESC
LIMIT 10;

-- ==============================================
-- 5. RÉPARTITION GÉOGRAPHIQUE DES STATIONS
-- ==============================================

-- Stations du centre-ville
SELECT 
    'Centre-ville' as zone,
    COUNT(*) as nombre_ravitaillements,
    SUM(montant_total) as total_montant
FROM Ravitaillement 
WHERE lieu LIKE '%Centre%' OR lieu LIKE '%Place%' OR lieu LIKE '%Avenue%' OR lieu LIKE '%Boulevard%';

-- Stations des quartiers résidentiels
SELECT 
    'Quartiers résidentiels' as zone,
    COUNT(*) as nombre_ravitaillements,
    SUM(montant_total) as total_montant
FROM Ravitaillement 
WHERE lieu LIKE '%Quartier%';

-- Stations des axes routiers
SELECT 
    'Axes routiers' as zone,
    COUNT(*) as nombre_ravitaillements,
    SUM(montant_total) as total_montant
FROM Ravitaillement 
WHERE lieu LIKE '%Route%';

-- Stations des zones commerciales
SELECT 
    'Zones commerciales' as zone,
    COUNT(*) as nombre_ravitaillements,
    SUM(montant_total) as total_montant
FROM Ravitaillement 
WHERE lieu LIKE '%Zone%' OR lieu LIKE '%Centre Commercial%' OR lieu LIKE '%Marché%';

-- ==============================================
-- 6. ÉVOLUTION DES PRIX PAR STATION
-- ==============================================

-- Évolution des prix par station (moyenne mensuelle)
SELECT 
    lieu,
    DATE_FORMAT(date_ravitaillement, '%Y-%m') as mois,
    AVG(prix_unitaire) as prix_moyen,
    COUNT(*) as nombre_ravitaillements
FROM Ravitaillement
WHERE date_ravitaillement >= '2024-09-01'
GROUP BY lieu, DATE_FORMAT(date_ravitaillement, '%Y-%m')
ORDER BY lieu, mois;

-- ==============================================
-- 7. ANALYSE DE PERFORMANCE PAR STATION
-- ==============================================

-- Performance des stations (volume et montant)
SELECT 
    lieu,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(quantite) as moyenne_litres_par_ravitaillement,
    AVG(prix_unitaire) as prix_moyen,
    MIN(date_ravitaillement) as premier_ravitaillement,
    MAX(date_ravitaillement) as dernier_ravitaillement
FROM Ravitaillement
GROUP BY lieu
ORDER BY total_montant DESC;

-- ==============================================
-- 8. RECOMMANDATIONS POUR L'OPTIMISATION
-- ==============================================

-- Stations avec le meilleur rapport qualité/prix
SELECT 
    lieu,
    AVG(prix_unitaire) as prix_moyen,
    COUNT(*) as nombre_ravitaillements,
    SUM(montant_total) as total_montant,
    ROUND(SUM(montant_total) / COUNT(*), 2) as montant_moyen_par_ravitaillement
FROM Ravitaillement
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
GROUP BY lieu
HAVING nombre_ravitaillements >= 3
ORDER BY prix_moyen ASC, nombre_ravitaillements DESC;

-- ==============================================
-- 9. EXPORT POUR L'APPLICATION
-- ==============================================

-- Requête pour l'export des données avec lieux en français
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
ORDER BY r.date_ravitaillement DESC;

-- ==============================================
-- 10. RÉSUMÉ DES MODIFICATIONS
-- ==============================================

-- Afficher le résumé des modifications
SELECT 
    'Lieux mis à jour en français' as modification,
    COUNT(DISTINCT lieu) as nombre_lieux_uniques,
    COUNT(*) as total_ravitaillements,
    MIN(date_ravitaillement) as periode_debut,
    MAX(date_ravitaillement) as periode_fin
FROM Ravitaillement;

-- ==============================================
-- 11. LIEUX DISPONIBLES POUR L'APPLICATION
-- ==============================================

-- Liste complète des lieux disponibles pour les formulaires
SELECT DISTINCT lieu as 'Lieux de ravitaillement disponibles'
FROM Ravitaillement 
ORDER BY lieu;

-- ==============================================
-- 12. STATISTIQUES FINALES
-- ==============================================

-- Statistiques globales après mise à jour
SELECT 
    'Statistiques globales' as type,
    COUNT(*) as total_ravitaillements,
    COUNT(DISTINCT lieu) as nombre_stations,
    COUNT(DISTINCT vehicule_matricule) as nombre_vehicules,
    COUNT(DISTINCT conducteur) as nombre_conducteurs,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant,
    AVG(prix_unitaire) as prix_moyen
FROM Ravitaillement;
