package service;

import model.Ravitaillement;
import model.Vehicule;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service spécialisé pour les statistiques et rapports
 * Optimisé pour l'application de gestion de carburant
 */
public class StatistiqueService {
    
    /**
     * Récupère les KPIs généraux pour le dashboard
     */
    public Map<String, Object> getKPIsGeneraux() {
        Map<String, Object> kpis = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(*) as nombre_ravitaillements, " +
                    "SUM(montant_total) as cout_total, " +
                    "SUM(quantite) as total_litres, " +
                    "AVG(montant_total / quantite) as prix_moyen " +
                    "FROM Ravitaillement";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                kpis.put("nombre_ravitaillements", rs.getInt("nombre_ravitaillements"));
                kpis.put("cout_total", rs.getDouble("cout_total"));
                kpis.put("total_litres", rs.getDouble("total_litres"));
                kpis.put("prix_moyen", rs.getDouble("prix_moyen"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kpis;
    }
    
    /**
     * Récupère la consommation moyenne réelle par véhicule
     */
    public List<Map<String, Object>> getConsommationReelleParVehicule() {
        List<Map<String, Object>> resultats = new ArrayList<>();
        String sql = "SELECT " +
                    "v.matricule, " +
                    "v.marque, " +
                    "v.modele, " +
                    "AVG((r2.quantite * 100.0) / (r2.kilometrage_releve - r1.kilometrage_releve)) as conso_moyenne_l_100km " +
                    "FROM Ravitaillement r1 " +
                    "JOIN Ravitaillement r2 ON r1.vehicule_matricule = r2.vehicule_matricule " +
                    "AND r2.date_ravitaillement > r1.date_ravitaillement " +
                    "JOIN Vehicule v ON r1.vehicule_matricule = v.matricule " +
                    "WHERE r2.kilometrage_releve > r1.kilometrage_releve " +
                    "GROUP BY v.matricule, v.marque, v.modele " +
                    "ORDER BY conso_moyenne_l_100km";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> vehicule = new HashMap<>();
                vehicule.put("matricule", rs.getString("matricule"));
                vehicule.put("marque", rs.getString("marque"));
                vehicule.put("modele", rs.getString("modele"));
                vehicule.put("conso_moyenne", rs.getDouble("conso_moyenne_l_100km"));
                resultats.add(vehicule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }
    
    /**
     * Récupère les statistiques par période (3, 6, 12 mois)
     */
    public List<Map<String, Object>> getStatistiquesParPeriode(int mois) {
        List<Map<String, Object>> resultats = new ArrayList<>();
        String sql = "SELECT " +
                    "DATE_FORMAT(date_ravitaillement, '%Y-%m') as mois, " +
                    "SUM(quantite) as total_litres, " +
                    "SUM(montant_total) as total_montant, " +
                    "COUNT(*) as nombre_ravitaillements, " +
                    "AVG(prix_unitaire) as prix_moyen " +
                    "FROM Ravitaillement " +
                    "WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                    "GROUP BY DATE_FORMAT(date_ravitaillement, '%Y-%m') " +
                    "ORDER BY mois";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, mois);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> statistique = new HashMap<>();
                    statistique.put("mois", rs.getString("mois"));
                    statistique.put("total_litres", rs.getDouble("total_litres"));
                    statistique.put("total_montant", rs.getDouble("total_montant"));
                    statistique.put("nombre_ravitaillements", rs.getInt("nombre_ravitaillements"));
                    statistique.put("prix_moyen", rs.getDouble("prix_moyen"));
                    resultats.add(statistique);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }
    
    /**
     * Récupère la répartition par véhicule
     */
    public List<Map<String, Object>> getRepartitionParVehicule() {
        List<Map<String, Object>> resultats = new ArrayList<>();
        String sql = "SELECT " +
                    "v.matricule, " +
                    "v.marque, " +
                    "v.modele, " +
                    "SUM(r.quantite) as total_litres, " +
                    "SUM(r.montant_total) as total_montant, " +
                    "COUNT(*) as nombre_ravitaillements, " +
                    "AVG(r.quantite) as moyenne_litres_par_ravitaillement " +
                    "FROM Ravitaillement r " +
                    "JOIN Vehicule v ON r.vehicule_matricule = v.matricule " +
                    "GROUP BY v.matricule, v.marque, v.modele " +
                    "ORDER BY total_montant DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> vehicule = new HashMap<>();
                vehicule.put("matricule", rs.getString("matricule"));
                vehicule.put("marque", rs.getString("marque"));
                vehicule.put("modele", rs.getString("modele"));
                vehicule.put("total_litres", rs.getDouble("total_litres"));
                vehicule.put("total_montant", rs.getDouble("total_montant"));
                vehicule.put("nombre_ravitaillements", rs.getInt("nombre_ravitaillements"));
                vehicule.put("moyenne_litres", rs.getDouble("moyenne_litres_par_ravitaillement"));
                resultats.add(vehicule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }
    
    /**
     * Récupère les ravitaillements par période pour l'impression
     */
    public List<Ravitaillement> getRavitaillementsParPeriode(LocalDateTime debut, LocalDateTime fin) {
        List<Ravitaillement> list = new ArrayList<>();
        String sql = "SELECT " +
                    "r.id_ravitaillement, r.date_ravitaillement, r.lieu, r.quantite, " +
                    "r.prix_unitaire, r.montant_total, r.kilometrage_releve, r.conducteur, " +
                    "v.matricule AS matricule_vehicule, v.marque, v.modele, " +
                    "c.nom_carburant, u.id_utilisateur, cc.id_carte " +
                    "FROM Ravitaillement r " +
                    "LEFT JOIN Vehicule v ON r.vehicule_matricule = v.matricule " +
                    "LEFT JOIN Carburant c ON v.id_carburant = c.id_carburant " +
                    "LEFT JOIN Utilisateur u ON r.id_utilisateur = u.id_utilisateur " +
                    "LEFT JOIN CarteCarburant cc ON r.id_carte = cc.id_carte " +
                    "WHERE r.date_ravitaillement BETWEEN ? AND ? " +
                    "ORDER BY r.date_ravitaillement DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(debut));
            ps.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ravitaillement r = new Ravitaillement();
                    r.setIdRavitaillement(rs.getInt("id_ravitaillement"));
                    r.setDateRavitaillement(rs.getTimestamp("date_ravitaillement").toLocalDateTime());
                    r.setLieu(rs.getString("lieu"));
                    r.setQuantite(rs.getDouble("quantite"));
                    r.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                    r.setMontantTotal(rs.getDouble("montant_total"));
                    r.setKilometrageReleve(rs.getInt("kilometrage_releve"));
                    r.setConducteur(rs.getString("conducteur"));
                    
                    // Associer le véhicule
                    Vehicule vehicule = new Vehicule();
                    vehicule.setMatricule(rs.getString("matricule_vehicule"));
                    vehicule.setMarque(rs.getString("marque"));
                    vehicule.setModele(rs.getString("modele"));
                    r.setVehicule(vehicule);
                    
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Récupère les statistiques pour une période donnée
     */
    public Map<String, Object> getStatistiquesPeriode(LocalDateTime debut, LocalDateTime fin) {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(*) as nombre_ravitaillements, " +
                    "SUM(quantite) as total_litres, " +
                    "SUM(montant_total) as total_montant, " +
                    "AVG(montant_total / quantite) as prix_moyen, " +
                    "MIN(date_ravitaillement) as date_debut, " +
                    "MAX(date_ravitaillement) as date_fin " +
                    "FROM Ravitaillement " +
                    "WHERE date_ravitaillement BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(debut));
            ps.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("nombre_ravitaillements", rs.getInt("nombre_ravitaillements"));
                    stats.put("total_litres", rs.getDouble("total_litres"));
                    stats.put("total_montant", rs.getDouble("total_montant"));
                    stats.put("prix_moyen", rs.getDouble("prix_moyen"));
                    stats.put("date_debut", rs.getTimestamp("date_debut"));
                    stats.put("date_fin", rs.getTimestamp("date_fin"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    /**
     * Récupère l'évolution des prix par type de carburant
     */
    public List<Map<String, Object>> getEvolutionPrix() {
        List<Map<String, Object>> resultats = new ArrayList<>();
        String sql = "SELECT " +
                    "c.nom_carburant, " +
                    "DATE_FORMAT(p.date_prix, '%Y-%m') as mois, " +
                    "p.prix, " +
                    "LAG(p.prix) OVER (PARTITION BY c.id_carburant ORDER BY p.date_prix) as prix_precedent, " +
                    "ROUND(((p.prix - LAG(p.prix) OVER (PARTITION BY c.id_carburant ORDER BY p.date_prix)) / " +
                    "LAG(p.prix) OVER (PARTITION BY c.id_carburant ORDER BY p.date_prix)) * 100, 2) as evolution_pourcentage " +
                    "FROM PrixCarburant p " +
                    "JOIN Carburant c ON p.id_carburant = c.id_carburant " +
                    "WHERE p.date_prix >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                    "ORDER BY c.nom_carburant, p.date_prix";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> prix = new HashMap<>();
                prix.put("nom_carburant", rs.getString("nom_carburant"));
                prix.put("mois", rs.getString("mois"));
                prix.put("prix", rs.getDouble("prix"));
                prix.put("prix_precedent", rs.getDouble("prix_precedent"));
                prix.put("evolution_pourcentage", rs.getDouble("evolution_pourcentage"));
                resultats.add(prix);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }
    
    /**
     * Récupère le top 5 des stations les plus utilisées
     */
    public List<Map<String, Object>> getTopStations() {
        List<Map<String, Object>> resultats = new ArrayList<>();
        String sql = "SELECT " +
                    "lieu, " +
                    "COUNT(*) as nombre_ravitaillements, " +
                    "SUM(quantite) as total_litres, " +
                    "SUM(montant_total) as total_montant, " +
                    "AVG(prix_unitaire) as prix_moyen " +
                    "FROM Ravitaillement " +
                    "GROUP BY lieu " +
                    "ORDER BY nombre_ravitaillements DESC " +
                    "LIMIT 5";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> station = new HashMap<>();
                station.put("lieu", rs.getString("lieu"));
                station.put("nombre_ravitaillements", rs.getInt("nombre_ravitaillements"));
                station.put("total_litres", rs.getDouble("total_litres"));
                station.put("total_montant", rs.getDouble("total_montant"));
                station.put("prix_moyen", rs.getDouble("prix_moyen"));
                resultats.add(station);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }
    
    /**
     * Récupère l'analyse saisonnière
     */
    public List<Map<String, Object>> getAnalyseSaisonniere() {
        List<Map<String, Object>> resultats = new ArrayList<>();
        String sql = "SELECT " +
                    "CASE " +
                    "WHEN MONTH(date_ravitaillement) IN (12, 1, 2) THEN 'Hiver' " +
                    "WHEN MONTH(date_ravitaillement) IN (3, 4, 5) THEN 'Printemps' " +
                    "WHEN MONTH(date_ravitaillement) IN (6, 7, 8) THEN 'Été' " +
                    "ELSE 'Automne' " +
                    "END as saison, " +
                    "COUNT(*) as nombre_ravitaillements, " +
                    "SUM(quantite) as total_litres, " +
                    "SUM(montant_total) as total_montant, " +
                    "AVG(quantite) as moyenne_litres_par_ravitaillement, " +
                    "AVG(prix_unitaire) as prix_moyen " +
                    "FROM Ravitaillement " +
                    "GROUP BY saison " +
                    "ORDER BY " +
                    "CASE saison " +
                    "WHEN 'Hiver' THEN 1 " +
                    "WHEN 'Printemps' THEN 2 " +
                    "WHEN 'Été' THEN 3 " +
                    "WHEN 'Automne' THEN 4 " +
                    "END";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> saison = new HashMap<>();
                saison.put("saison", rs.getString("saison"));
                saison.put("nombre_ravitaillements", rs.getInt("nombre_ravitaillements"));
                saison.put("total_litres", rs.getDouble("total_litres"));
                saison.put("total_montant", rs.getDouble("total_montant"));
                saison.put("moyenne_litres", rs.getDouble("moyenne_litres_par_ravitaillement"));
                saison.put("prix_moyen", rs.getDouble("prix_moyen"));
                resultats.add(saison);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }
    
    /**
     * Récupère les véhicules avec consommation anormale
     */
    public List<Map<String, Object>> getVehiculesConsommationAnormale() {
        List<Map<String, Object>> resultats = new ArrayList<>();
        String sql = "SELECT " +
                    "v.matricule, " +
                    "v.marque, " +
                    "v.modele, " +
                    "v.conso_theorique, " +
                    "AVG((r2.quantite * 100.0) / (r2.kilometrage_releve - r1.kilometrage_releve)) as conso_reelle, " +
                    "ROUND(((AVG((r2.quantite * 100.0) / (r2.kilometrage_releve - r1.kilometrage_releve)) - v.conso_theorique) / v.conso_theorique) * 100, 2) as ecart_pourcentage " +
                    "FROM Ravitaillement r1 " +
                    "JOIN Ravitaillement r2 ON r1.vehicule_matricule = r2.vehicule_matricule " +
                    "AND r2.date_ravitaillement > r1.date_ravitaillement " +
                    "JOIN Vehicule v ON r1.vehicule_matricule = v.matricule " +
                    "WHERE r2.kilometrage_releve > r1.kilometrage_releve " +
                    "GROUP BY v.matricule, v.marque, v.modele, v.conso_theorique " +
                    "HAVING ecart_pourcentage > 20 " +
                    "ORDER BY ecart_pourcentage DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> vehicule = new HashMap<>();
                vehicule.put("matricule", rs.getString("matricule"));
                vehicule.put("marque", rs.getString("marque"));
                vehicule.put("modele", rs.getString("modele"));
                vehicule.put("conso_theorique", rs.getDouble("conso_theorique"));
                vehicule.put("conso_reelle", rs.getDouble("conso_reelle"));
                vehicule.put("ecart_pourcentage", rs.getDouble("ecart_pourcentage"));
                resultats.add(vehicule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }
}
