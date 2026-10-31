package service;

import model.Role;
import model.Utilisateur;
import java.sql.*;

public class UtilisateurService {

    public Utilisateur authentifier(String login, String motDePasse) {
        String sql =
            "SELECT u.id_utilisateur, u.nom, u.login, u.mot_de_passe, r.id_role, r.nom AS role_nom " +
            "FROM Utilisateur u " +
            "JOIN Role r ON u.id_role = r.id_role " +
            "WHERE u.login = ? AND u.mot_de_passe = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, motDePasse); // En prod : utiliser hash (BCrypt)

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role(rs.getInt("id_role"), rs.getString("role_nom"));
                    return new Utilisateur(
                        rs.getInt("id_utilisateur"),
                        rs.getString("nom"),
                        rs.getString("login"),
                        rs.getString("mot_de_passe"),
                        role
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
