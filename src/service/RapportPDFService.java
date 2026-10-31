package service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import model.Ravitaillement;

/**
 * Service avancé pour la génération de rapports PDF avec statistiques
 */
public class RapportPDFService {
    
    private final StatistiqueService statistiqueService = new StatistiqueService();
    
    /**
     * Génère un rapport PDF complet avec statistiques
     */
    public void genererRapportComplet(LocalDateTime debut, LocalDateTime fin, String chemin) throws Exception {
        Document doc = new Document(PageSize.A4.rotate()); // Format paysage pour plus d'espace
        PdfWriter.getInstance(doc, new FileOutputStream(chemin));
        doc.open();
        
        // En-tête
        ajouterEnTete(doc, debut, fin);
        
        // Résumé exécutif
        ajouterResumeExecutif(doc, debut, fin);
        
        // Statistiques par période
        ajouterStatistiquesPeriode(doc, debut, fin);
        
        // Répartition par véhicule
        ajouterRepartitionVehicules(doc);
        
        // Top stations
        ajouterTopStations(doc);
        
        // Analyse saisonnière
        ajouterAnalyseSaisonniere(doc);
        
        // Évolution des prix
        ajouterEvolutionPrix(doc);
        
        // Véhicules avec consommation anormale
        ajouterVehiculesConsommationAnormale(doc);
        
        // Détail des ravitaillements
        ajouterDetailRavitaillements(doc, debut, fin);
        
        doc.close();
    }
    
    private void ajouterEnTete(Document doc, LocalDateTime debut, LocalDateTime fin) throws Exception {
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
        Paragraph title = new Paragraph("RAPPORT DE GESTION CARBURANT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        doc.add(title);
        
        // Sous-titre
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.DARK_GRAY);
        String periode = String.format("Période: %s au %s", 
            debut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        Paragraph subtitle = new Paragraph(periode, subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        doc.add(subtitle);
        
        // Informations de génération
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph info = new Paragraph(String.format("Rapport généré le %s - Région Oujda, Maroc", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))), infoFont);
        info.setAlignment(Element.ALIGN_CENTER);
        info.setSpacingAfter(20);
        doc.add(info);
        
        // Ligne de séparation
        doc.add(new com.lowagie.text.pdf.draw.LineSeparator());
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterResumeExecutif(Document doc, LocalDateTime debut, LocalDateTime fin) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("1. RÉSUMÉ EXÉCUTIF", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        Map<String, Object> stats = statistiqueService.getStatistiquesPeriode(debut, fin);
        
        // Tableau de résumé
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        ajouterCelluleResume(table, "Nombre de ravitaillements", stats.get("nombre_ravitaillements").toString());
        ajouterCelluleResume(table, "Total litres consommés", String.format("%.2f L", (Double) stats.get("total_litres")));
        ajouterCelluleResume(table, "Montant total", String.format("%.2f DH", (Double) stats.get("total_montant")));
        ajouterCelluleResume(table, "Prix moyen", String.format("%.3f DH/L", (Double) stats.get("prix_moyen")));
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterStatistiquesPeriode(Document doc, LocalDateTime debut, LocalDateTime fin) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("2. STATISTIQUES PAR PÉRIODE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        // Comparaison 3, 6, 12 mois
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Période", true);
        ajouterCelluleTableau(table, "Ravitaillements", true);
        ajouterCelluleTableau(table, "Litres", true);
        ajouterCelluleTableau(table, "Montant (DH)", true);
        ajouterCelluleTableau(table, "Prix moyen", true);
        ajouterCelluleTableau(table, "Période", true);
        
        // Données pour 3, 6, 12 mois
        for (int mois : new int[]{3, 6, 12}) {
            List<Map<String, Object>> stats = statistiqueService.getStatistiquesParPeriode(mois);
            if (!stats.isEmpty()) {
                Map<String, Object> stat = stats.get(0);
                ajouterCelluleTableau(table, mois + " derniers mois", false);
                ajouterCelluleTableau(table, stat.get("nombre_ravitaillements").toString(), false);
                ajouterCelluleTableau(table, String.format("%.2f", (Double) stat.get("total_litres")), false);
                ajouterCelluleTableau(table, String.format("%.2f", (Double) stat.get("total_montant")), false);
                ajouterCelluleTableau(table, String.format("%.3f", (Double) stat.get("prix_moyen")), false);
                ajouterCelluleTableau(table, stat.get("mois").toString(), false);
            }
        }
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterRepartitionVehicules(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("3. RÉPARTITION PAR VÉHICULE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> repartition = statistiqueService.getRepartitionParVehicule();
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Matricule", true);
        ajouterCelluleTableau(table, "Marque/Modèle", true);
        ajouterCelluleTableau(table, "Ravitaillements", true);
        ajouterCelluleTableau(table, "Litres", true);
        ajouterCelluleTableau(table, "Montant (DH)", true);
        ajouterCelluleTableau(table, "Moyenne (L)", true);
        
        // Données
        for (Map<String, Object> vehicule : repartition) {
            ajouterCelluleTableau(table, vehicule.get("matricule").toString(), false);
            ajouterCelluleTableau(table, vehicule.get("marque") + " " + vehicule.get("modele"), false);
            ajouterCelluleTableau(table, vehicule.get("nombre_ravitaillements").toString(), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) vehicule.get("total_litres")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) vehicule.get("total_montant")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) vehicule.get("moyenne_litres")), false);
        }
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterTopStations(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("4. TOP 5 DES STATIONS", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> topStations = statistiqueService.getTopStations();
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Station", true);
        ajouterCelluleTableau(table, "Ravitaillements", true);
        ajouterCelluleTableau(table, "Litres", true);
        ajouterCelluleTableau(table, "Montant (DH)", true);
        ajouterCelluleTableau(table, "Prix moyen", true);
        
        // Données
        for (Map<String, Object> station : topStations) {
            ajouterCelluleTableau(table, station.get("lieu").toString(), false);
            ajouterCelluleTableau(table, station.get("nombre_ravitaillements").toString(), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) station.get("total_litres")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) station.get("total_montant")), false);
            ajouterCelluleTableau(table, String.format("%.3f", (Double) station.get("prix_moyen")), false);
        }
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterAnalyseSaisonniere(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("5. ANALYSE SAISONNIÈRE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> saisons = statistiqueService.getAnalyseSaisonniere();
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Saison", true);
        ajouterCelluleTableau(table, "Ravitaillements", true);
        ajouterCelluleTableau(table, "Litres", true);
        ajouterCelluleTableau(table, "Montant (DH)", true);
        ajouterCelluleTableau(table, "Moyenne (L)", true);
        ajouterCelluleTableau(table, "Prix moyen", true);
        
        // Données
        for (Map<String, Object> saison : saisons) {
            ajouterCelluleTableau(table, saison.get("saison").toString(), false);
            ajouterCelluleTableau(table, saison.get("nombre_ravitaillements").toString(), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) saison.get("total_litres")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) saison.get("total_montant")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) saison.get("moyenne_litres")), false);
            ajouterCelluleTableau(table, String.format("%.3f", (Double) saison.get("prix_moyen")), false);
        }
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterEvolutionPrix(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("6. ÉVOLUTION DES PRIX", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> evolution = statistiqueService.getEvolutionPrix();
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Carburant", true);
        ajouterCelluleTableau(table, "Mois", true);
        ajouterCelluleTableau(table, "Prix (DH)", true);
        ajouterCelluleTableau(table, "Prix précédent", true);
        ajouterCelluleTableau(table, "Évolution %", true);
        
        // Données (limitées aux 20 dernières entrées)
        int limit = Math.min(20, evolution.size());
        for (int i = 0; i < limit; i++) {
            Map<String, Object> prix = evolution.get(i);
            ajouterCelluleTableau(table, prix.get("nom_carburant").toString(), false);
            ajouterCelluleTableau(table, prix.get("mois").toString(), false);
            ajouterCelluleTableau(table, String.format("%.3f", (Double) prix.get("prix")), false);
            ajouterCelluleTableau(table, String.format("%.3f", (Double) prix.get("prix_precedent")), false);
            ajouterCelluleTableau(table, String.format("%.2f", (Double) prix.get("evolution_pourcentage")), false);
        }
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterVehiculesConsommationAnormale(Document doc) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("7. VÉHICULES AVEC CONSOMMATION ANORMALE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Map<String, Object>> vehiculesAnormaux = statistiqueService.getVehiculesConsommationAnormale();
        
        if (vehiculesAnormaux.isEmpty()) {
            Paragraph message = new Paragraph("Aucun véhicule avec consommation anormale détecté.", 
                FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GREEN));
            doc.add(message);
        } else {
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // En-têtes
            ajouterCelluleTableau(table, "Matricule", true);
            ajouterCelluleTableau(table, "Marque/Modèle", true);
            ajouterCelluleTableau(table, "Conso théorique", true);
            ajouterCelluleTableau(table, "Conso réelle", true);
            ajouterCelluleTableau(table, "Écart %", true);
            ajouterCelluleTableau(table, "Recommandation", true);
            
            // Données
            for (Map<String, Object> vehicule : vehiculesAnormaux) {
                ajouterCelluleTableau(table, vehicule.get("matricule").toString(), false);
                ajouterCelluleTableau(table, vehicule.get("marque") + " " + vehicule.get("modele"), false);
                ajouterCelluleTableau(table, String.format("%.2f L/100km", (Double) vehicule.get("conso_theorique")), false);
                ajouterCelluleTableau(table, String.format("%.2f L/100km", (Double) vehicule.get("conso_reelle")), false);
                ajouterCelluleTableau(table, String.format("%.1f%%", (Double) vehicule.get("ecart_pourcentage")), false);
                ajouterCelluleTableau(table, "Vérification nécessaire", false);
            }
            
            doc.add(table);
        }
        
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterDetailRavitaillements(Document doc, LocalDateTime debut, LocalDateTime fin) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("8. DÉTAIL DES RAVITAILLEMENTS", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        List<Ravitaillement> ravitaillements = statistiqueService.getRavitaillementsParPeriode(debut, fin);
        
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Date", true);
        ajouterCelluleTableau(table, "Véhicule", true);
        ajouterCelluleTableau(table, "Conducteur", true);
        ajouterCelluleTableau(table, "Station", true);
        ajouterCelluleTableau(table, "Quantité (L)", true);
        ajouterCelluleTableau(table, "Prix/L (DH)", true);
        ajouterCelluleTableau(table, "Montant (DH)", true);
        ajouterCelluleTableau(table, "Km", true);
        
        // Données (limitées aux 50 premiers pour éviter un PDF trop long)
        int limit = Math.min(50, ravitaillements.size());
        for (int i = 0; i < limit; i++) {
            Ravitaillement r = ravitaillements.get(i);
            ajouterCelluleTableau(table, r.getDateRavitaillement().toLocalDate().toString(), false);
            ajouterCelluleTableau(table, r.getVehicule().getMatricule(), false);
            ajouterCelluleTableau(table, r.getConducteur(), false);
            ajouterCelluleTableau(table, r.getLieu(), false);
            ajouterCelluleTableau(table, String.format("%.2f", r.getQuantite()), false);
            ajouterCelluleTableau(table, String.format("%.3f", r.getPrixUnitaire()), false);
            ajouterCelluleTableau(table, String.format("%.2f", r.getMontantTotal()), false);
            ajouterCelluleTableau(table, String.valueOf(r.getKilometrageReleve()), false);
        }
        
        doc.add(table);
        
        if (ravitaillements.size() > 50) {
            Paragraph note = new Paragraph(String.format("Note: Seuls les 50 premiers ravitaillements sont affichés. Total: %d ravitaillements.", 
                ravitaillements.size()), FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
            doc.add(note);
        }
    }
    
    private void ajouterCelluleResume(PdfPTable table, String label, String valeur) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cellLabel.setBackgroundColor(Color.LIGHT_GRAY);
        cellLabel.setPadding(8);
        table.addCell(cellLabel);
        
        PdfPCell cellValue = new PdfPCell(new Phrase(valeur, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        cellValue.setPadding(8);
        table.addCell(cellValue);
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
