package service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Carburant;

public class CarburantService {

    /**
     * Récupère tous les types de carburant depuis la base de données.
     * @return Une liste d'objets Carburant.
     */
    public List<Carburant> getAll() {
        List<Carburant> carburants = new ArrayList<>();
        String sql = "SELECT id_carburant, nom_carburant FROM Carburant ORDER BY nom_carburant";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_carburant");
                String nom = rs.getString("nom_carburant");
                carburants.add(new Carburant(id, nom));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des types de carburant : " + e.getMessage());
            e.printStackTrace();
        }
        return carburants;
    }
}
