package service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Vehicule;

/**
 * Service spécialisé pour l'export PDF des véhicules avec leurs informations détaillées
 */
public class ExportVehiculesPDFService {
    
    /**
     * Génère un rapport PDF complet des véhicules avec leurs statistiques
     */
    public void genererRapportVehiculesPDF(String chemin) throws Exception {
        Document doc = new Document(PageSize.A4.rotate()); // Format paysage
        PdfWriter.getInstance(doc, new FileOutputStream(chemin));
        doc.open();
        
        // En-tête
        ajouterEnTete(doc);
        
        // Liste des véhicules avec informations détaillées
        ajouterListeVehicules(doc);
        
        // Statistiques par véhicule
        ajouterStatistiquesVehicules(doc);
        
        // Consommation réelle vs théorique
        ajouterComparaisonConsommation(doc);
        
        // Historique des ravitaillements par véhicule
        ajouterHistoriqueRavitaillements(doc);
        
        doc.close();
    }
    
    private void ajouterEnTete(Document doc) throws Exception {
        try {
            // Ajouter le logo en en-tête
            Image logo = Image.getInstance(getClass().getResource("/images/entete_logo.png"));
            logo.scaleToFit(200, 80); // Ajuster la taille du logo
            logo.setAlignment(Element.ALIGN_CENTER);
            doc.add(logo);
            
            // Espacement après le logo
            doc.add(new Paragraph(" "));
            
        } catch (IOException e) {
            // Si le logo n'est pas trouvé, continuer sans logo
            System.out.println("Logo non trouvé, génération du rapport sans logo");
        }
        
        // Titre principal
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);
        Paragraph title = new Paragraph("RAPPORT DES VÉHICULES", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        doc.add(title);
        
        // Sous-titre
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.DARK_GRAY);
        Paragraph subtitle = new Paragraph("Flotte de véhicules - Région Oujda, Maroc", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        doc.add(subtitle);
        
        // Informations de génération
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph info = new Paragraph(String.format("Rapport généré le %s", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))), infoFont);
        info.setAlignment(Element.ALIGN_CENTER);
        info.setSpacingAfter(20);
        doc.add(info);
        
        // Ligne de séparation
        doc.add(new com.lowagie.text.pdf.draw.LineSeparator());
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterListeVehicules(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("1. INFORMATIONS DES VÉHICULES", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> vehicules = getVehiculesAvecInformations();
        
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Matricule", true);
        ajouterCelluleTableau(table, "Marque", true);
        ajouterCelluleTableau(table, "Modèle", true);
        ajouterCelluleTableau(table, "Km Initial", true);
        ajouterCelluleTableau(table, "Km Actuel", true);
        ajouterCelluleTableau(table, "Date Mise Service", true);
        ajouterCelluleTableau(table, "Conso Théorique", true);
        ajouterCelluleTableau(table, "Type Carburant", true);
        
        // Données
        for (Map<String, Object> vehicule : vehicules) {
            ajouterCelluleTableau(table, vehicule.get("matricule").toString(), false);
            ajouterCelluleTableau(table, vehicule.get("marque").toString(), false);
            ajouterCelluleTableau(table, vehicule.get("modele").toString(), false);
            ajouterCelluleTableau(table, vehicule.get("kilometrage_initial").toString(), false);
            ajouterCelluleTableau(table, vehicule.get("kilometrage_actuel").toString(), false);
            ajouterCelluleTableau(table, vehicule.get("date_mise_service").toString(), false);
            ajouterCelluleTableau(table, String.format("%.2f L/100km", (Double) vehicule.get("conso_theorique")), false);
            ajouterCelluleTableau(table, vehicule.get("nom_carburant").toString(), false);
        }
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterStatistiquesVehicules(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("2. STATISTIQUES PAR VÉHICULE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> statistiques = getStatistiquesParVehicule();
        
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Matricule", true);
        ajouterCelluleTableau(table, "Ravitaillements", true);
        ajouterCelluleTableau(table, "Total Litres", true);
        ajouterCelluleTableau(table, "Montant Total (DH)", true);
        ajouterCelluleTableau(table, "Moyenne (L)", true);
        ajouterCelluleTableau(table, "Prix Moyen (DH/L)", true);
        ajouterCelluleTableau(table, "Km Parcourus", true);
        
        // Données
        for (Map<String, Object> stat : statistiques) {
            ajouterCelluleTableau(table, stat.get("matricule").toString(), false);
            ajouterCelluleTableau(table, stat.get("nombre_ravitaillements").toString(), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) stat.get("total_litres")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) stat.get("total_montant")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) stat.get("moyenne_litres")), false);
            ajouterCelluleTableau(table, String.format("%.3f", (Double) stat.get("prix_moyen")), false);
            ajouterCelluleTableau(table, stat.get("km_parcourus").toString(), false);
        }
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterComparaisonConsommation(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("3. CONSOMMATION RÉELLE VS THÉORIQUE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> consommation = getComparaisonConsommation();
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Matricule", true);
        ajouterCelluleTableau(table, "Conso Théorique", true);
        ajouterCelluleTableau(table, "Conso Réelle", true);
        ajouterCelluleTableau(table, "Écart (L/100km)", true);
        ajouterCelluleTableau(table, "Écart %", true);
        ajouterCelluleTableau(table, "Statut", true);
        
        // Données
        for (Map<String, Object> conso : consommation) {
            ajouterCelluleTableau(table, conso.get("matricule").toString(), false);
            ajouterCelluleTableau(table, String.format("%.2f L/100km", (Double) conso.get("conso_theorique")), false);
            ajouterCelluleTableau(table, String.format("%.2f L/100km", (Double) conso.get("conso_reelle")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) conso.get("ecart_litre")), false);
            
            double ecartPourcentage = (Double) conso.get("ecart_pourcentage");
            String statut = ecartPourcentage > 20 ? "Anormal" : 
                           ecartPourcentage > 10 ? "Eleve" : "Normal";
            
            ajouterCelluleTableau(table, String.format("%.1f%%", ecartPourcentage), false);
            ajouterCelluleTableau(table, statut, false);
        }
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterHistoriqueRavitaillements(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("4. HISTORIQUE DES RAVITAILLEMENTS", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> historique = getHistoriqueRavitaillements();
        
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Date", true);
        ajouterCelluleTableau(table, "Matricule", true);
        ajouterCelluleTableau(table, "Conducteur", true);
        ajouterCelluleTableau(table, "Station", true);
        ajouterCelluleTableau(table, "Quantité (L)", true);
        ajouterCelluleTableau(table, "Prix/L (DH)", true);
        ajouterCelluleTableau(table, "Montant (DH)", true);
        ajouterCelluleTableau(table, "Km", true);
        
        // Données (limitées aux 30 derniers ravitaillements)
        int limit = Math.min(30, historique.size());
        for (int i = 0; i < limit; i++) {
            Map<String, Object> ravitaillement = historique.get(i);
            ajouterCelluleTableau(table, ravitaillement.get("date_ravitaillement").toString(), false);
            ajouterCelluleTableau(table, ravitaillement.get("matricule").toString(), false);
            ajouterCelluleTableau(table, ravitaillement.get("conducteur").toString(), false);
            ajouterCelluleTableau(table, ravitaillement.get("lieu").toString(), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) ravitaillement.get("quantite")), false);
            ajouterCelluleTableau(table, String.format("%.3f", (Double) ravitaillement.get("prix_unitaire")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) ravitaillement.get("montant_total")), false);
            ajouterCelluleTableau(table, ravitaillement.get("kilometrage_releve").toString(), false);
        }
        
        doc.add(table);
        
        if (historique.size() > 30) {
            Paragraph note = new Paragraph(String.format("Note: Seuls les 30 derniers ravitaillements sont affichés. Total: %d ravitaillements.", 
                historique.size()), FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
            doc.add(note);
        }
    }
    
    // Méthodes pour récupérer les données depuis la base
    private List<Map<String, Object>> getVehiculesAvecInformations() {
        List<Map<String, Object>> vehicules = new ArrayList<>();
        String sql = "SELECT v.*, c.nom_carburant " +
                    "FROM Vehicule v " +
                    "LEFT JOIN Carburant c ON v.id_carburant = c.id_carburant " +
                    "ORDER BY v.matricule";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> vehicule = new java.util.HashMap<>();
                vehicule.put("matricule", rs.getString("matricule"));
                vehicule.put("marque", rs.getString("marque"));
                vehicule.put("modele", rs.getString("modele"));
                vehicule.put("kilometrage_initial", rs.getInt("kilometrage_initial"));
                vehicule.put("kilometrage_actuel", rs.getInt("kilometrage_actuel"));
                vehicule.put("date_mise_service", rs.getDate("date_mise_service"));
                vehicule.put("conso_theorique", rs.getDouble("conso_theorique"));
                vehicule.put("nom_carburant", rs.getString("nom_carburant"));
                vehicules.add(vehicule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicules;
    }
    
    private List<Map<String, Object>> getStatistiquesParVehicule() {
        List<Map<String, Object>> statistiques = new ArrayList<>();
        String sql = "SELECT " +
                    "v.matricule, " +
                    "COUNT(r.id_ravitaillement) as nombre_ravitaillements, " +
                    "COALESCE(SUM(r.quantite), 0) as total_litres, " +
                    "COALESCE(SUM(r.montant_total), 0) as total_montant, " +
                    "COALESCE(AVG(r.quantite), 0) as moyenne_litres, " +
                    "COALESCE(AVG(r.prix_unitaire), 0) as prix_moyen, " +
                    "(v.kilometrage_actuel - v.kilometrage_initial) as km_parcourus " +
                    "FROM Vehicule v " +
                    "LEFT JOIN Ravitaillement r ON v.matricule = r.vehicule_matricule " +
                    "GROUP BY v.matricule, v.kilometrage_actuel, v.kilometrage_initial " +
                    "ORDER BY v.matricule";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> stat = new java.util.HashMap<>();
                stat.put("matricule", rs.getString("matricule"));
                stat.put("nombre_ravitaillements", rs.getInt("nombre_ravitaillements"));
                stat.put("total_litres", rs.getDouble("total_litres"));
                stat.put("total_montant", rs.getDouble("total_montant"));
                stat.put("moyenne_litres", rs.getDouble("moyenne_litres"));
                stat.put("prix_moyen", rs.getDouble("prix_moyen"));
                stat.put("km_parcourus", rs.getInt("km_parcourus"));
                statistiques.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statistiques;
    }
    
    private List<Map<String, Object>> getComparaisonConsommation() {
        List<Map<String, Object>> consommation = new ArrayList<>();
        String sql = "SELECT " +
                    "v.matricule, " +
                    "v.conso_theorique, " +
                    "COALESCE(AVG((r2.quantite * 100.0) / (r2.kilometrage_releve - r1.kilometrage_releve)), 0) as conso_reelle " +
                    "FROM Vehicule v " +
                    "LEFT JOIN Ravitaillement r1 ON v.matricule = r1.vehicule_matricule " +
                    "LEFT JOIN Ravitaillement r2 ON r1.vehicule_matricule = r2.vehicule_matricule " +
                    "AND r2.date_ravitaillement > r1.date_ravitaillement " +
                    "AND r2.kilometrage_releve > r1.kilometrage_releve " +
                    "GROUP BY v.matricule, v.conso_theorique " +
                    "ORDER BY v.matricule";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> conso = new java.util.HashMap<>();
                double consoTheorique = rs.getDouble("conso_theorique");
                double consoReelle = rs.getDouble("conso_reelle");
                
                conso.put("matricule", rs.getString("matricule"));
                conso.put("conso_theorique", consoTheorique);
                conso.put("conso_reelle", consoReelle);
                conso.put("ecart_litre", consoReelle - consoTheorique);
                conso.put("ecart_pourcentage", consoTheorique > 0 ? ((consoReelle - consoTheorique) / consoTheorique) * 100 : 0);
                consommation.add(conso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consommation;
    }
    
    private List<Map<String, Object>> getHistoriqueRavitaillements() {
        List<Map<String, Object>> historique = new ArrayList<>();
        String sql = "SELECT " +
                    "r.date_ravitaillement, " +
                    "r.vehicule_matricule as matricule, " +
                    "r.conducteur, " +
                    "r.lieu, " +
                    "r.quantite, " +
                    "r.prix_unitaire, " +
                    "r.montant_total, " +
                    "r.kilometrage_releve " +
                    "FROM Ravitaillement r " +
                    "ORDER BY r.date_ravitaillement DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> ravitaillement = new java.util.HashMap<>();
                ravitaillement.put("date_ravitaillement", rs.getTimestamp("date_ravitaillement").toLocalDateTime().toLocalDate());
                ravitaillement.put("matricule", rs.getString("matricule"));
                ravitaillement.put("conducteur", rs.getString("conducteur"));
                ravitaillement.put("lieu", rs.getString("lieu"));
                ravitaillement.put("quantite", rs.getDouble("quantite"));
                ravitaillement.put("prix_unitaire", rs.getDouble("prix_unitaire"));
                ravitaillement.put("montant_total", rs.getDouble("montant_total"));
                ravitaillement.put("kilometrage_releve", rs.getInt("kilometrage_releve"));
                historique.add(ravitaillement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historique;
    }
    
    private void ajouterCelluleTableau(PdfPTable table, String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        if (isHeader) {
            cell.setBackgroundColor(Color.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
        } else {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(4);
        }
        table.addCell(cell);
    }
}
