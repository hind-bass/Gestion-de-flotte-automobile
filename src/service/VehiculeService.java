package service;

import model.Vehicule;
import model.Carburant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculeService {

    // ✅ CORRIGÉ : La requête SQL a été entièrement réécrite pour correspondre
    // aux colonnes de votre table et au modèle Vehicule.java.
    public void ajouter(Vehicule v) {
        String sql = "INSERT INTO Vehicule (matricule, marque, modele, kilometrage_initial, kilometrage_actuel, date_mise_service, conso_theorique, id_carburant) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, v.getMatricule());
            ps.setString(2, v.getMarque());
            ps.setString(3, v.getModele());
            ps.setInt(4, v.getKilometrageActuel()); // Au début, le km initial est le km actuel
            ps.setInt(5, v.getKilometrageActuel());
            ps.setDate(6, v.getDateMiseService() != null ? Date.valueOf(v.getDateMiseService()) : null);
            ps.setDouble(7, v.getConsoTheorique());
            ps.setInt(8, v.getCarburant().getIdCarburant());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du véhicule : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ CORRIGÉ : Jointure ajoutée pour récupérer les informations complètes du carburant.
    public List<Vehicule> getAll() {
        List<Vehicule> list = new ArrayList<>();
        String sql = "SELECT v.*, c.nom_carburant FROM Vehicule v LEFT JOIN Carburant c ON v.id_carburant = c.id_carburant ORDER BY v.marque, v.modele";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Vehicule v = new Vehicule();
                v.setMatricule(rs.getString("matricule"));
                v.setMarque(rs.getString("marque"));
                v.setModele(rs.getString("modele"));
                v.setKilometrageInitial(rs.getInt("kilometrage_initial"));
                v.setKilometrageActuel(rs.getInt("kilometrage_actuel"));
                if (rs.getDate("date_mise_service") != null) {
                    v.setDateMiseService(rs.getDate("date_mise_service").toLocalDate());
                }
                v.setConsoTheorique(rs.getDouble("conso_theorique"));
                
                Carburant c = new Carburant(rs.getInt("id_carburant"), rs.getString("nom_carburant"));
                v.setCarburant(c);
                
                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ CORRIGÉ : Implémentation de la méthode de mise à jour.
    public void mettreAJour(Vehicule v) {
        String sql = "UPDATE Vehicule SET marque = ?, modele = ?, kilometrage_actuel = ?, conso_theorique = ?, id_carburant = ? WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getMarque());
            ps.setString(2, v.getModele());
            ps.setInt(3, v.getKilometrageActuel());
            ps.setDouble(4, v.getConsoTheorique());
            ps.setInt(5, v.getCarburant().getIdCarburant());
            ps.setString(6, v.getMatricule());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ CORRIGÉ : Implémentation de la méthode de suppression.
    public void supprimer(String matricule) {
        String sql = "DELETE FROM Vehicule WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, matricule);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            // Gérer le cas où le véhicule est utilisé dans un ravitaillement (clé étrangère)
            System.err.println("Erreur de suppression : " + e.getMessage());
        }
    }
}
