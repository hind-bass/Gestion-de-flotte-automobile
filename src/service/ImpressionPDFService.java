package service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.Ravitaillement;

/**
 * Service d'impression PDF avec logo pour les reçus et rapports
 */
public class ImpressionPDFService {
    
    /**
     * Génère un reçu PDF pour un ravitaillement
     */
    public void genererRecuPDF(Ravitaillement ravitaillement, String chemin) throws Exception {
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, new FileOutputStream(chemin));
        doc.open();
        
        // En-tête avec logo
        ajouterEnTeteRecu(doc);
        
        // Informations du reçu
        ajouterInformationsRecu(doc, ravitaillement);
        
        // Détail du ravitaillement
        ajouterDetailRavitaillement(doc, ravitaillement);
        
        // Pied de page
        ajouterPiedPage(doc);
        
        doc.close();
    }
    
    /**
     * Génère un rapport PDF des ravitaillements avec logo
     */
    public void genererRapportPDF(List<Ravitaillement> ravitaillements, String chemin) throws Exception {
        Document doc = new Document(PageSize.A4.rotate()); // Format paysage
        PdfWriter.getInstance(doc, new FileOutputStream(chemin));
        doc.open();
        
        // En-tête avec logo
        ajouterEnTeteRapport(doc);
        
        // Résumé des statistiques
        ajouterResumeStatistiques(doc, ravitaillements);
        
        // Tableau des ravitaillements
        ajouterTableauRavitaillements(doc, ravitaillements);
        
        // Statistiques par véhicule
        ajouterStatistiquesParVehicule(doc, ravitaillements);
        
        doc.close();
    }
    
    private void ajouterEnTeteRecu(Document doc) throws Exception {
        try {
            // Ajouter le logo en en-tête
            Image logo = Image.getInstance(getClass().getResource("/images/entete_logo.png"));
            logo.scaleToFit(150, 60); // Taille plus petite pour le reçu
            logo.setAlignment(Element.ALIGN_CENTER);
            doc.add(logo);
            
            // Espacement après le logo
            doc.add(new Paragraph(" "));
            
        } catch (IOException e) {
            // Si le logo n'est pas trouvé, continuer sans logo
            System.out.println("Logo non trouvé, génération du reçu sans logo");
        }
        
        // Titre du reçu
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
        Paragraph title = new Paragraph("REÇU DE RAVITAILLEMENT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        doc.add(title);
        
        // Informations de l'organisation
        Font orgFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
        Paragraph org = new Paragraph("Région de l'Oriental - Oujda, Maroc", orgFont);
        org.setAlignment(Element.ALIGN_CENTER);
        org.setSpacingAfter(10);
        doc.add(org);
        
        // Ligne de séparation
        doc.add(new com.lowagie.text.pdf.draw.LineSeparator());
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterEnTeteRapport(Document doc) throws Exception {
        try {
            // Ajouter le logo en en-tête
            Image logo = Image.getInstance(getClass().getResource("/images/entete_logo.png"));
            logo.scaleToFit(200, 80); // Taille normale pour le rapport
            logo.setAlignment(Element.ALIGN_CENTER);
            doc.add(logo);
            
            // Espacement après le logo
            doc.add(new Paragraph(" "));
            
        } catch (IOException e) {
            // Si le logo n'est pas trouvé, continuer sans logo
            System.out.println("Logo non trouvé, génération du rapport sans logo");
        }
        
        // Titre du rapport
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);
        Paragraph title = new Paragraph("RAPPORT DES RAVITAILLEMENTS", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        doc.add(title);
        
        // Sous-titre
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.DARK_GRAY);
        Paragraph subtitle = new Paragraph("Région de l'Oriental - Oujda, Maroc", subtitleFont);
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
    
    private void ajouterInformationsRecu(Document doc, Ravitaillement ravitaillement) throws Exception {
        // Numéro du reçu
        Font numFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph numRecu = new Paragraph(String.format("Reçu N°: %d", ravitaillement.getIdRavitaillement()), numFont);
        numRecu.setAlignment(Element.ALIGN_RIGHT);
        numRecu.setSpacingAfter(10);
        doc.add(numRecu);
        
        // Date et heure
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
        Paragraph date = new Paragraph(String.format("Date: %s", 
            ravitaillement.getDateRavitaillement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))), dateFont);
        date.setSpacingAfter(15);
        doc.add(date);
    }
    
    private void ajouterDetailRavitaillement(Document doc, Ravitaillement ravitaillement) throws Exception {
        // Tableau des détails
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        
        // En-têtes
        ajouterCelluleRecu(table, "Détail", "Valeur", true);
        
        // Données
        ajouterCelluleRecu(table, "Véhicule", 
            (ravitaillement.getVehicule() != null) ? ravitaillement.getVehicule().getMatricule() : "N/A", false);
        ajouterCelluleRecu(table, "Conducteur", 
            (ravitaillement.getConducteur() != null && !ravitaillement.getConducteur().trim().isEmpty()) 
                ? ravitaillement.getConducteur() : "Non spécifié", false);
        ajouterCelluleRecu(table, "Station", 
            (ravitaillement.getLieu() != null && !ravitaillement.getLieu().trim().isEmpty()) 
                ? ravitaillement.getLieu() : "Non spécifié", false);
        ajouterCelluleRecu(table, "Quantité", String.format("%.2f litres", ravitaillement.getQuantite()), false);
        ajouterCelluleRecu(table, "Prix unitaire", String.format("%.3f DH/L", ravitaillement.getPrixUnitaire()), false);
        ajouterCelluleRecu(table, "Montant total", String.format("%.2f DH", ravitaillement.getMontantTotal()), false);
        ajouterCelluleRecu(table, "Kilométrage", String.valueOf(ravitaillement.getKilometrageReleve()), false);
        
        doc.add(table);
        
        // Signature
        doc.add(new Paragraph(" "));
        Font signatureFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph signature = new Paragraph("Signature: _________________________", signatureFont);
        signature.setAlignment(Element.ALIGN_RIGHT);
        signature.setSpacingBefore(30);
        doc.add(signature);
    }
    
    private void ajouterResumeStatistiques(Document doc, List<Ravitaillement> ravitaillements) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("RÉSUMÉ DES STATISTIQUES", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        // Calcul des statistiques
        double totalLitres = ravitaillements.stream().mapToDouble(Ravitaillement::getQuantite).sum();
        double totalMontant = ravitaillements.stream().mapToDouble(Ravitaillement::getMontantTotal).sum();
        double prixMoyen = (totalLitres > 0) ? totalMontant / totalLitres : 0;
        int nombreRavitaillements = ravitaillements.size();
        
        // Tableau de résumé
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        ajouterCelluleRecu(table, "Nombre de ravitaillements", String.valueOf(nombreRavitaillements), false);
        ajouterCelluleRecu(table, "Total litres consommés", String.format("%.2f L", totalLitres), false);
        ajouterCelluleRecu(table, "Montant total", String.format("%.2f DH", totalMontant), false);
        ajouterCelluleRecu(table, "Prix moyen par litre", String.format("%.3f DH/L", prixMoyen), false);
        
        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterTableauRavitaillements(Document doc, List<Ravitaillement> ravitaillements) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("DÉTAIL DES RAVITAILLEMENTS", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);

        // Tableau
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // En-têtes
        ajouterCelluleTableau(table, "Date", true);
        ajouterCelluleTableau(table, "Véhicule", true);
        ajouterCelluleTableau(table, "Conducteur", true);
        ajouterCelluleTableau(table, "Station", true);
        ajouterCelluleTableau(table, "Quantité (L)", true);
        ajouterCelluleTableau(table, "Prix/L (DH)", true);
        ajouterCelluleTableau(table, "Montant (DH)", true);
        ajouterCelluleTableau(table, "Km", true);

        // Données
        for (Ravitaillement r : ravitaillements) {
            // Date formatée en français
            String dateFormatee = r.getDateRavitaillement().toLocalDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            ajouterCelluleTableau(table, dateFormatee, false);
            
            // Matricule du véhicule
            String matricule = (r.getVehicule() != null) ? r.getVehicule().getMatricule() : "N/A";
            ajouterCelluleTableau(table, matricule, false);
            
            // Conducteur
            String conducteur = (r.getConducteur() != null && !r.getConducteur().trim().isEmpty()) 
                ? r.getConducteur() : "Non spécifié";
            ajouterCelluleTableau(table, conducteur, false);
            
            // Station
            String station = (r.getLieu() != null && !r.getLieu().trim().isEmpty()) 
                ? r.getLieu() : "Non spécifié";
            ajouterCelluleTableau(table, station, false);
            
            // Quantité
            ajouterCelluleTableau(table, String.format("%.2f", r.getQuantite()), false);
            
            // Prix unitaire
            ajouterCelluleTableau(table, String.format("%.3f", r.getPrixUnitaire()), false);
            
            // Montant total
            ajouterCelluleTableau(table, String.format("%.2f", r.getMontantTotal()), false);
            
            // Kilométrage
            ajouterCelluleTableau(table, String.valueOf(r.getKilometrageReleve()), false);
        }

        doc.add(table);
        doc.add(new Paragraph(" "));
    }
    
    private void ajouterStatistiquesParVehicule(Document doc, List<Ravitaillement> ravitaillements) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
        Paragraph sectionTitle = new Paragraph("STATISTIQUES PAR VÉHICULE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        doc.add(sectionTitle);
        
        // Grouper par véhicule
        java.util.Map<String, List<Ravitaillement>> ravitsParVehicule = ravitaillements.stream()
            .filter(r -> r.getVehicule() != null && r.getVehicule().getMatricule() != null)
            .collect(java.util.stream.Collectors.groupingBy(r -> r.getVehicule().getMatricule()));
        
        if (ravitsParVehicule.isEmpty()) {
            Paragraph message = new Paragraph("Aucune donnée de véhicule disponible.", 
                FontFactory.getFont(FontFactory.HELVETICA, 12, Color.RED));
            doc.add(message);
            return;
        }
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes
        ajouterCelluleTableau(table, "Véhicule", true);
        ajouterCelluleTableau(table, "Ravitaillements", true);
        ajouterCelluleTableau(table, "Total Litres", true);
        ajouterCelluleTableau(table, "Montant Total (DH)", true);
        ajouterCelluleTableau(table, "Moyenne (L)", true);
        
        // Données
        for (java.util.Map.Entry<String, List<Ravitaillement>> entry : ravitsParVehicule.entrySet()) {
            List<Ravitaillement> ravitsVehicule = entry.getValue();
            double totalLitres = ravitsVehicule.stream().mapToDouble(Ravitaillement::getQuantite).sum();
            double totalMontant = ravitsVehicule.stream().mapToDouble(Ravitaillement::getMontantTotal).sum();
            double moyenne = totalLitres / ravitsVehicule.size();
            
            ajouterCelluleTableau(table, entry.getKey(), false);
            ajouterCelluleTableau(table, String.valueOf(ravitsVehicule.size()), false);
            ajouterCelluleTableau(table, String.format("%.2f", totalLitres), false);
            ajouterCelluleTableau(table, String.format("%.2f", totalMontant), false);
            ajouterCelluleTableau(table, String.format("%.2f", moyenne), false);
        }
        
        doc.add(table);
    }
    
    private void ajouterPiedPage(Document doc) throws Exception {
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));
        
        Font piedFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
        Paragraph pied = new Paragraph("Ce reçu a été généré automatiquement par le système de gestion de carburant", piedFont);
        pied.setAlignment(Element.ALIGN_CENTER);
        doc.add(pied);
    }
    
    private void ajouterCelluleRecu(PdfPTable table, String label, String valeur, boolean isHeader) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cellLabel.setBackgroundColor(isHeader ? Color.GRAY : Color.LIGHT_GRAY);
        cellLabel.setPadding(8);
        table.addCell(cellLabel);
        
        PdfPCell cellValue = new PdfPCell(new Phrase(valeur, 
            FontFactory.getFont(FontFactory.HELVETICA, 12)));
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
