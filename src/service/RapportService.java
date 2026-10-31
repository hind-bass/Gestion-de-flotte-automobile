package service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.Ravitaillement;

public class RapportService {

    /**
     * Génère un rapport PDF des ravitaillements avec statistiques
     */
    public void genererRapportPDF(List<Ravitaillement> ravits, String chemin) throws Exception {
        Document doc = new Document(PageSize.A4.rotate()); // Format paysage
        PdfWriter.getInstance(doc, new FileOutputStream(chemin));
        doc.open();

        // En-tête
        ajouterEnTete(doc);
        
        // Résumé des statistiques
        ajouterResumeStatistiques(doc, ravits);

        // Tableau des ravitaillements
        ajouterTableauRavitaillements(doc, ravits);
        
        // Statistiques par véhicule
        ajouterStatistiquesParVehicule(doc, ravits);

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

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);
        Paragraph title = new Paragraph("RAPPORT DES RAVITAILLEMENTS", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        doc.add(title);

        // Sous-titre
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.DARK_GRAY);
        Paragraph subtitle = new Paragraph("Gestion Carburant - Région Oujda, Maroc", subtitleFont);
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
    
    private void ajouterResumeStatistiques(Document doc, List<Ravitaillement> ravits) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("RÉSUMÉ DES STATISTIQUES", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        // Calcul des statistiques
        double totalLitres = ravits.stream().mapToDouble(Ravitaillement::getQuantite).sum();
        double totalMontant = ravits.stream().mapToDouble(Ravitaillement::getMontantTotal).sum();
        double prixMoyen = (totalLitres > 0) ? totalMontant / totalLitres : 0;
        int nombreRavitaillements = ravits.size();
        
        // Calcul des statistiques supplémentaires
        long nombreVehicules = ravits.stream()
            .filter(r -> r.getVehicule() != null && r.getVehicule().getMatricule() != null)
            .map(r -> r.getVehicule().getMatricule())
            .distinct()
            .count();
        
        long nombreConducteurs = ravits.stream()
            .filter(r -> r.getConducteur() != null && !r.getConducteur().trim().isEmpty())
            .map(Ravitaillement::getConducteur)
            .distinct()
            .count();
        
        long nombreStations = ravits.stream()
            .filter(r -> r.getLieu() != null && !r.getLieu().trim().isEmpty())
            .map(Ravitaillement::getLieu)
            .distinct()
            .count();
        
        double moyenneParRavitaillement = (nombreRavitaillements > 0) ? totalLitres / nombreRavitaillements : 0;
        
        // Tableau de résumé
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        ajouterCelluleResume(table, "Nombre de ravitaillements", String.valueOf(nombreRavitaillements));
        ajouterCelluleResume(table, "Total litres consommés", String.format("%.2f L", totalLitres));
        ajouterCelluleResume(table, "Montant total", String.format("%.2f DH", totalMontant));
        ajouterCelluleResume(table, "Prix moyen par litre", String.format("%.3f DH/L", prixMoyen));
        ajouterCelluleResume(table, "Moyenne par ravitaillement", String.format("%.2f L", moyenneParRavitaillement));
        ajouterCelluleResume(table, "Nombre de véhicules", String.valueOf(nombreVehicules));
        ajouterCelluleResume(table, "Nombre de conducteurs", String.valueOf(nombreConducteurs));
        ajouterCelluleResume(table, "Nombre de stations", String.valueOf(nombreStations));
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterTableauRavitaillements(Document doc, List<Ravitaillement> ravits) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("DÉTAIL DES RAVITAILLEMENTS", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);

        // Tableau
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // En-têtes
        addCellToTable(table, "Date", true);
        addCellToTable(table, "Véhicule", true);
        addCellToTable(table, "Conducteur", true);
        addCellToTable(table, "Station", true);
        addCellToTable(table, "Quantité (L)", true);
        addCellToTable(table, "Prix/L (DH)", true);
        addCellToTable(table, "Montant (DH)", true);
        addCellToTable(table, "Km", true);

        // Données
        for (Ravitaillement r : ravits) {
            // Date formatée en français
            String dateFormatee = r.getDateRavitaillement().toLocalDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            addCellToTable(table, dateFormatee, false);
            
            // Matricule du véhicule
            String matricule = (r.getVehicule() != null) ? r.getVehicule().getMatricule() : "N/A";
            addCellToTable(table, matricule, false);
            
            // Conducteur (avec gestion des valeurs null)
            String conducteur = (r.getConducteur() != null && !r.getConducteur().trim().isEmpty()) 
                ? r.getConducteur() : "Non spécifié";
            addCellToTable(table, conducteur, false);
            
            // Station (avec gestion des valeurs null)
            String station = (r.getLieu() != null && !r.getLieu().trim().isEmpty()) 
                ? r.getLieu() : "Non spécifié";
            addCellToTable(table, station, false);
            
            // Quantité
            addCellToTable(table, String.format("%.2f", r.getQuantite()), false);
            
            // Prix unitaire
            addCellToTable(table, String.format("%.3f", r.getPrixUnitaire()), false);
            
            // Montant total
            addCellToTable(table, String.format("%.2f", r.getMontantTotal()), false);
            
            // Kilométrage
            addCellToTable(table, String.valueOf(r.getKilometrageReleve()), false);
        }

        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterStatistiquesParVehicule(Document doc, List<Ravitaillement> ravits) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("STATISTIQUES PAR VÉHICULE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        // Grouper par véhicule (avec gestion des véhicules null)
        java.util.Map<String, List<Ravitaillement>> ravitsParVehicule = ravits.stream()
            .filter(r -> r.getVehicule() != null && r.getVehicule().getMatricule() != null)
            .collect(java.util.stream.Collectors.groupingBy(r -> r.getVehicule().getMatricule()));
        
        if (ravitsParVehicule.isEmpty()) {
            Paragraph message = new Paragraph("Aucune donnée de véhicule disponible.", 
                FontFactory.getFont(FontFactory.HELVETICA, 12, Color.RED));
            doc.add(message);
            return;
        }
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        addCellToTable(table, "Véhicule", true);
        addCellToTable(table, "Nombre de ravitaillements", true);
        addCellToTable(table, "Total litres", true);
        addCellToTable(table, "Montant total (DH)", true);
        addCellToTable(table, "Moyenne par ravitaillement (L)", true);
        addCellToTable(table, "Prix moyen (DH/L)", true);
        
        // Données
        for (java.util.Map.Entry<String, List<Ravitaillement>> entry : ravitsParVehicule.entrySet()) {
            List<Ravitaillement> ravitsVehicule = entry.getValue();
            double totalLitres = ravitsVehicule.stream().mapToDouble(Ravitaillement::getQuantite).sum();
            double totalMontant = ravitsVehicule.stream().mapToDouble(Ravitaillement::getMontantTotal).sum();
            double moyenneLitres = totalLitres / ravitsVehicule.size();
            double prixMoyen = totalMontant / totalLitres;
            
            addCellToTable(table, entry.getKey(), false);
            addCellToTable(table, String.valueOf(ravitsVehicule.size()), false);
            addCellToTable(table, String.format("%.2f", totalLitres), false);
            addCellToTable(table, String.format("%.2f", totalMontant), false);
            addCellToTable(table, String.format("%.2f", moyenneLitres), false);
            addCellToTable(table, String.format("%.3f", prixMoyen), false);
        }
        
        doc.add(table);
        
        // Ajouter un résumé des conducteurs
        ajouterResumeConducteurs(doc, ravits);
    }
    
    private void ajouterResumeConducteurs(Document doc, List<Ravitaillement> ravits) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("RÉSUMÉ PAR CONDUCTEUR", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        // Grouper par conducteur
        java.util.Map<String, List<Ravitaillement>> ravitsParConducteur = ravits.stream()
            .filter(r -> r.getConducteur() != null && !r.getConducteur().trim().isEmpty())
            .collect(java.util.stream.Collectors.groupingBy(Ravitaillement::getConducteur));
        
        if (ravitsParConducteur.isEmpty()) {
            Paragraph message = new Paragraph("Aucune donnée de conducteur disponible.", 
                FontFactory.getFont(FontFactory.HELVETICA, 12, Color.RED));
            doc.add(message);
            return;
        }
        
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        addCellToTable(table, "Conducteur", true);
        addCellToTable(table, "Nombre de ravitaillements", true);
        addCellToTable(table, "Total litres", true);
        addCellToTable(table, "Montant total (DH)", true);
        
        // Données
        for (java.util.Map.Entry<String, List<Ravitaillement>> entry : ravitsParConducteur.entrySet()) {
            List<Ravitaillement> ravitsConducteur = entry.getValue();
            double totalLitres = ravitsConducteur.stream().mapToDouble(Ravitaillement::getQuantite).sum();
            double totalMontant = ravitsConducteur.stream().mapToDouble(Ravitaillement::getMontantTotal).sum();
            
            addCellToTable(table, entry.getKey(), false);
            addCellToTable(table, String.valueOf(ravitsConducteur.size()), false);
            addCellToTable(table, String.format("%.2f", totalLitres), false);
            addCellToTable(table, String.format("%.2f", totalMontant), false);
        }
        
        doc.add(table);
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

    private void addCellToTable(PdfPTable table, String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        if (isHeader) {
            cell.setBackgroundColor(Color.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        table.addCell(cell);
    }
}