-- ==============================================
-- MISE À JOUR SIMPLE DES LIEUX EN FRANÇAIS
-- Région d'Oujda, Maroc
-- ==============================================

USE gestion_carburant;

-- ==============================================
-- MISE À JOUR DES LIEUX ARABES VERS FRANÇAIS
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
-- VÉRIFICATION DES MODIFICATIONS
-- ==============================================

-- Afficher tous les lieux uniques après mise à jour
SELECT DISTINCT lieu, COUNT(*) as nombre_ravitaillements
FROM Ravitaillement 
GROUP BY lieu 
ORDER BY nombre_ravitaillements DESC;

-- ==============================================
-- STATISTIQUES RAPIDES
-- ==============================================

-- Top 5 des stations les plus utilisées
SELECT 
    lieu,
    COUNT(*) as nombre_ravitaillements,
    SUM(quantite) as total_litres,
    SUM(montant_total) as total_montant
FROM Ravitaillement
GROUP BY lieu
ORDER BY nombre_ravitaillements DESC
LIMIT 5;
