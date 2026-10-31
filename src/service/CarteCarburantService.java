package service;

import model.CarteCarburant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarteCarburantService {

    /**
     * Récupère toutes les cartes de carburant depuis la base
     */
    public List<CarteCarburant> getAll() {
        List<CarteCarburant> cartes = new ArrayList<>();
        String sql = "SELECT id_carte, numero_carte, titulaire, plafond, statut "
                + "FROM CarteCarburant "
                + "ORDER BY numero_carte";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CarteCarburant carte = new CarteCarburant();
                carte.setIdCarte(rs.getInt("id_carte"));
                carte.setNumeroCarte(rs.getString("numero_carte"));
                carte.setTitulaire(rs.getString("titulaire"));
                carte.setPlafond(rs.getDouble("plafond"));
                carte.setStatut(rs.getString("statut")); // 'active' ou 'inactive'

                cartes.add(carte);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des cartes de carburant : " + e.getMessage());
            e.printStackTrace();
        }
        return cartes;
    }

    /**
     * Ajoute une nouvelle carte de carburant
     */
    public void ajouter(CarteCarburant carte) {
        String sql = "INSERT INTO CarteCarburant (numero_carte, titulaire, plafond, statut) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, carte.getNumeroCarte());
            ps.setString(2, carte.getTitulaire());
            ps.setDouble(3, carte.getPlafond());
            ps.setString(4, carte.getStatut() != null ? carte.getStatut() : "active");

            ps.executeUpdate();
            System.out.println("Carte ajoutée : " + carte.getNumeroCarte());

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la carte : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Met à jour une carte existante
     */
    public void mettreAJour(CarteCarburant carte) {
        String sql = "UPDATE CarteCarburant "
                + "SET titulaire = ?, plafond = ?, statut = ? "
                + "WHERE numero_carte = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, carte.getTitulaire());
            ps.setDouble(2, carte.getPlafond());
            ps.setString(3, carte.getStatut());
            ps.setString(4, carte.getNumeroCarte());

            ps.executeUpdate();
            System.out.println("Carte mise à jour : " + carte.getNumeroCarte());

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la carte : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprime une carte par son numéro
     */
    public void supprimer(String numeroCarte) {
        String sql = "DELETE FROM CarteCarburant WHERE numero_carte = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroCarte);
            int lignes = ps.executeUpdate();

            if (lignes > 0) {
                System.out.println("Carte supprimée : " + numeroCarte);
            } else {
                System.out.println("Aucune carte trouvée avec le numéro : " + numeroCarte);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la carte : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Recherche une carte par son numéro
     */
    public CarteCarburant findByNumero(String numeroCarte) {
        String sql = "SELECT * FROM CarteCarburant WHERE numero_carte = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroCarte);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CarteCarburant carte = new CarteCarburant();
                    carte.setIdCarte(rs.getInt("id_carte"));
                    carte.setNumeroCarte(rs.getString("numero_carte"));
                    carte.setTitulaire(rs.getString("titulaire"));
                    carte.setPlafond(rs.getDouble("plafond"));
                    carte.setStatut(rs.getString("statut"));
                    return carte;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la carte : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Compte le nombre total de cartes
     */
    public int count() {
        String sql = "SELECT COUNT(*) AS total FROM CarteCarburant";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
