package service;

import model.Ravitaillement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Vehicule;

public class RavitaillementService {

    public List<Ravitaillement> getAll() {
        List<Ravitaillement> list = new ArrayList<>();
        String sql =
            "SELECT r.id_ravitaillement, r.date_ravitaillement, r.lieu, r.quantite, " +
            "r.prix_unitaire, r.montant_total, r.kilometrage_releve, r.conducteur, " +
            "v.matricule AS matricule_vehicule, u.id_utilisateur, c.id_carte " +
            "FROM Ravitaillement r " +
            "LEFT JOIN Vehicule v ON r.vehicule_matricule = v.matricule " +
            "LEFT JOIN Utilisateur u ON r.id_utilisateur = u.id_utilisateur " +
            "LEFT JOIN CarteCarburant c ON r.id_carte = c.id_carte " +
            "ORDER BY r.date_ravitaillement DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

                // Associer le véhicule (minimal)
                Vehicule vehicule = new Vehicule();
                vehicule.setMatricule(rs.getString("matricule_vehicule"));
                r.setVehicule(vehicule);

                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void ajouter(Ravitaillement r) {
        String sql =
            "INSERT INTO Ravitaillement " +
            "(date_ravitaillement, lieu, quantite, prix_unitaire, montant_total, " +
            "kilometrage_releve, vehicule_matricule, id_utilisateur, id_carte, conducteur) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(r.getDateRavitaillement()));
            ps.setString(2, r.getLieu());
            ps.setDouble(3, r.getQuantite());
            ps.setDouble(4, r.getPrixUnitaire());
            ps.setDouble(5, r.getMontantTotal());
            ps.setInt(6, r.getKilometrageReleve());
            ps.setString(7, r.getVehicule().getMatricule());
            ps.setObject(8, r.getUtilisateur() != null ? r.getUtilisateur().getIdUtilisateur() : null);
            ps.setObject(9, r.getCarteCarburant() != null ? r.getCarteCarburant().getIdCarte() : null);
            ps.setString(10, r.getConducteur());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mettreAJour(Ravitaillement r) {
        String sql =
            "UPDATE Ravitaillement SET " +
            "date_ravitaillement = ?, lieu = ?, quantite = ?, prix_unitaire = ?, " +
            "montant_total = ?, kilometrage_releve = ?, vehicule_matricule = ?, " +
            "id_utilisateur = ?, id_carte = ?, conducteur = ? " +
            "WHERE id_ravitaillement = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(r.getDateRavitaillement()));
            ps.setString(2, r.getLieu());
            ps.setDouble(3, r.getQuantite());
            ps.setDouble(4, r.getPrixUnitaire());
            ps.setDouble(5, r.getMontantTotal());
            ps.setInt(6, r.getKilometrageReleve());
            ps.setString(7, r.getVehicule().getMatricule());
            ps.setObject(8, r.getUtilisateur() != null ? r.getUtilisateur().getIdUtilisateur() : null);
            ps.setObject(9, r.getCarteCarburant() != null ? r.getCarteCarburant().getIdCarte() : null);
            ps.setString(10, r.getConducteur());
            ps.setInt(11, r.getIdRavitaillement());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimer(int idRavitaillement) {
        String sql = "DELETE FROM Ravitaillement WHERE id_ravitaillement = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRavitaillement);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Ravitaillement findById(int id) {
        String sql =
            "SELECT r.id_ravitaillement, r.date_ravitaillement, r.lieu, r.quantite, " +
            "r.prix_unitaire, r.montant_total, r.kilometrage_releve, r.conducteur, " +
            "v.matricule AS matricule_vehicule, u.id_utilisateur, c.id_carte " +
            "FROM Ravitaillement r " +
            "LEFT JOIN Vehicule v ON r.vehicule_matricule = v.matricule " +
            "LEFT JOIN Utilisateur u ON r.id_utilisateur = u.id_utilisateur " +
            "LEFT JOIN CarteCarburant c ON r.id_carte = c.id_carte " +
            "WHERE r.id_ravitaillement = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ravitaillement r = new Ravitaillement();
                    r.setIdRavitaillement(rs.getInt("id_ravitaillement"));
                    r.setDateRavitaillement(rs.getTimestamp("date_ravitaillement").toLocalDateTime());
                    r.setLieu(rs.getString("lieu"));
                    r.setQuantite(rs.getDouble("quantite"));
                    r.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                    r.setMontantTotal(rs.getDouble("montant_total"));
                    r.setKilometrageReleve(rs.getInt("kilometrage_releve"));
                    r.setConducteur(rs.getString("conducteur"));

                    Vehicule vehicule = new Vehicule();
                    vehicule.setMatricule(rs.getString("matricule_vehicule"));
                    r.setVehicule(vehicule);

                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Ravitaillement> getByVehicule(String matricule) {
        List<Ravitaillement> list = new ArrayList<>();
        String sql =
            "SELECT r.id_ravitaillement, r.date_ravitaillement, r.lieu, r.quantite, " +
            "r.prix_unitaire, r.montant_total, r.kilometrage_releve, r.conducteur, " +
            "v.matricule AS matricule_vehicule, u.id_utilisateur, c.id_carte " +
            "FROM Ravitaillement r " +
            "LEFT JOIN Vehicule v ON r.vehicule_matricule = v.matricule " +
            "LEFT JOIN Utilisateur u ON r.id_utilisateur = u.id_utilisateur " +
            "LEFT JOIN CarteCarburant c ON r.id_carte = c.id_carte " +
            "WHERE v.matricule = ? " +
            "ORDER BY r.date_ravitaillement DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricule);
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

                    Vehicule vehicule = new Vehicule();
                    vehicule.setMatricule(rs.getString("matricule_vehicule"));
                    r.setVehicule(vehicule);

                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
