package service;

import model.Ravitaillement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImpressionService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Genere un en-tete stylise avec logo pour les documents
     */
    private String genererEnteteLogo(String titre, int largeur) {
        StringBuilder entete = new StringBuilder();
        String ligne = "=".repeat(largeur - 4);
        
        entete.append("=".repeat(largeur)).append("\n");
        entete.append("  +").append(ligne).append("+\n");
        entete.append("  |").append(centrerTexte("🚗 GESTION CARBURANT 🚗", largeur - 4)).append("|\n");
        entete.append("  |").append(centrerTexte("", largeur - 4)).append("|\n");
        entete.append("  |").append(centrerTexte(titre, largeur - 4)).append("|\n");
        entete.append("  +").append(ligne).append("+\n");
        entete.append("=".repeat(largeur)).append("\n\n");
        
        return entete.toString();
    }
    
    /**
     * Centre un texte dans une largeur donnee
     */
    private String centrerTexte(String texte, int largeur) {
        if (texte.length() >= largeur) {
            return texte.substring(0, largeur);
        }
        
        int espaces = largeur - texte.length();
        int espacesGauche = espaces / 2;
        int espacesDroite = espaces - espacesGauche;
        
        return " ".repeat(espacesGauche) + texte + " ".repeat(espacesDroite);
    }
    
    /**
     * Genere un recu de ravitaillement au format texte
     */
    public String genererRecuRavitaillement(Ravitaillement ravitaillement) {
        StringBuilder recu = new StringBuilder();
        
        // En-tete avec logo stylise
        recu.append(genererEnteteLogo("RECU DE RAVITAILLEMENT", 70));
        
        // Informations du ravitaillement
        recu.append("Date et heure: ").append(ravitaillement.getDateRavitaillement().format(DATE_FORMATTER)).append("\n");
        recu.append("Lieu: ").append(ravitaillement.getLieu()).append("\n");
        recu.append("N° Ravitaillement: ").append(ravitaillement.getIdRavitaillement()).append("\n\n");
        
        // Informations du vehicule
        if (ravitaillement.getVehicule() != null) {
            recu.append("Vehicule: ").append(ravitaillement.getVehicule().getMatricule()).append("\n");
            recu.append("Marque/Modele: ").append(ravitaillement.getVehicule().getMarque()).append(" ").append(ravitaillement.getVehicule().getModele()).append("\n");
        }
        
        // Informations du conducteur
        recu.append("Conducteur: ").append(ravitaillement.getConducteur()).append("\n");
        
        // Informations de la carte carburant
        if (ravitaillement.getCarteCarburant() != null) {
            recu.append("Carte carburant: ").append(ravitaillement.getCarteCarburant().getNumeroCarte()).append("\n");
            recu.append("Titulaire: ").append(ravitaillement.getCarteCarburant().getTitulaire()).append("\n");
            recu.append("Plafond: ").append(String.format("%.2f", ravitaillement.getCarteCarburant().getPlafond())).append(" DH\n");
        }
        
        recu.append("\n");
        recu.append("-".repeat(60)).append("\n");
        
        // Details du carburant
        recu.append("Quantite: ").append(String.format("%.2f", ravitaillement.getQuantite())).append(" litres\n");
        recu.append("Prix unitaire: ").append(String.format("%.2f", ravitaillement.getPrixUnitaire())).append(" DH/litre\n");
        recu.append("Montant total: ").append(String.format("%.2f", ravitaillement.getMontantTotal())).append(" DH\n");
        recu.append("Kilometrage: ").append(ravitaillement.getKilometrageReleve()).append(" km\n\n");
        
        // Calcul de la consommation
        recu.append("INFORMATIONS DE CONSOMMATION:\n");
        recu.append("-".repeat(35)).append("\n");
        
        // Calcul de la periode de consommation (exemple: carte de 2000 DH pour 2 mois)
        if (ravitaillement.getCarteCarburant() != null) {
            double plafond = ravitaillement.getCarteCarburant().getPlafond();
            double prixMoyen = ravitaillement.getPrixUnitaire();
            double litresPossibles = plafond / prixMoyen;
            
            recu.append("Plafond de la carte: ").append(String.format("%.2f", plafond)).append(" DH\n");
            recu.append("Prix moyen du carburant: ").append(String.format("%.2f", prixMoyen)).append(" DH/litre\n");
            recu.append("Litres possibles avec le plafond: ").append(String.format("%.2f", litresPossibles)).append(" litres\n");
            
            // Estimation de la duree (basee sur la consommation moyenne)
            double consommationMoyenne = 8.0; // litres/100km (valeur par defaut)
            double autonomieKm = litresPossibles / consommationMoyenne * 100;
            recu.append("Autonomie estimee: ").append(String.format("%.0f", autonomieKm)).append(" km\n");
            
            // Calcul de la duree estimee de la carte
            double consommationMensuelle = 200.0; // litres par mois (valeur par defaut)
            double dureeMois = litresPossibles / consommationMensuelle;
            recu.append("Duree estimee de la carte: ").append(String.format("%.1f", dureeMois)).append(" mois\n");
        }
        
        recu.append("\n");
        recu.append("=".repeat(70)).append("\n");
        recu.append("           RECU GENERE LE ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        recu.append("=".repeat(70)).append("\n");
        
        return recu.toString();
    }
    
    /**
     * Genere un rapport de consommation pour une periode donnee
     */
    public String genererRapportConsommation(List<Ravitaillement> ravitaillements, LocalDateTime dateDebut, LocalDateTime dateFin) {
        StringBuilder rapport = new StringBuilder();
        
        // En-tete avec logo stylise
        rapport.append(genererEnteteLogo("RAPPORT DE CONSOMMATION", 80));
        
        rapport.append("Periode: ").append(dateDebut.format(DATE_ONLY_FORMATTER))
               .append(" au ").append(dateFin.format(DATE_ONLY_FORMATTER)).append("\n\n");
        
        // Tableau des ravitaillements
        rapport.append(String.format("%-5s %-12s %-15s %-12s %-8s %-12s %-12s\n", 
                "ID", "Date", "Conducteur", "Vehicule", "Litres", "Prix/L", "Total"));
        rapport.append("-".repeat(90)).append("\n");
        
        double totalLitres = 0;
        double totalMontant = 0;
        
        for (Ravitaillement r : ravitaillements) {
            String vehicule = r.getVehicule() != null ? r.getVehicule().getMatricule() : "N/A";
            rapport.append(String.format("%-5d %-12s %-15s %-12s %-8.2f %-12.2f %-12.2f\n",
                    r.getIdRavitaillement(),
                    r.getDateRavitaillement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    r.getConducteur(),
                    vehicule,
                    r.getQuantite(),
                    r.getPrixUnitaire(),
                    r.getMontantTotal()));
            
            totalLitres += r.getQuantite();
            totalMontant += r.getMontantTotal();
        }
        
        rapport.append("-".repeat(90)).append("\n");
        rapport.append(String.format("%-47s %-8.2f %-12s %-12.2f\n", 
                "TOTAUX:", totalLitres, "", totalMontant));
        
        rapport.append("\n");
        rapport.append("STATISTIQUES DETAILLEES:\n");
        rapport.append("-".repeat(30)).append("\n");
        rapport.append("Nombre de ravitaillements: ").append(ravitaillements.size()).append("\n");
        rapport.append("Consommation moyenne par ravitaillement: ").append(String.format("%.2f", totalLitres / ravitaillements.size())).append(" litres\n");
        rapport.append("Cout moyen par litre: ").append(String.format("%.2f", totalMontant / totalLitres)).append(" DH\n");
        
        // Calculs supplementaires
        if (ravitaillements.size() > 1) {
            long jours = java.time.Duration.between(dateDebut, dateFin).toDays();
            double consommationQuotidienne = totalLitres / Math.max(jours, 1);
            double coutQuotidien = totalMontant / Math.max(jours, 1);
            
            rapport.append("Consommation quotidienne moyenne: ").append(String.format("%.2f", consommationQuotidienne)).append(" litres/jour\n");
            rapport.append("Cout quotidien moyen: ").append(String.format("%.2f", coutQuotidien)).append(" DH/jour\n");
        }
        
        rapport.append("\n");
        rapport.append("=".repeat(70)).append("\n");
        
        return rapport.toString();
    }
    
    /**
     * Sauvegarde le recu dans un fichier texte
     */
    public boolean sauvegarderRecu(String contenu, String nomFichier) {
        try {
            File fichier = new File(nomFichier);
            try (PrintWriter writer = new PrintWriter(new FileWriter(fichier))) {
                writer.print(contenu);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Genere un recu detaille avec historique des ravitaillements
     */
    public String genererRecuDetaille(Ravitaillement ravitaillement, List<Ravitaillement> historique) {
        StringBuilder recu = new StringBuilder();
        
        // En-tete avec logo stylise
        recu.append(genererEnteteLogo("RECU DETAILLE AVEC HISTORIQUE", 80));
        
        // Informations du ravitaillement actuel
        recu.append("RAVITAILLEMENT ACTUEL:\n");
        recu.append("-".repeat(40)).append("\n");
        recu.append("Date et heure: ").append(ravitaillement.getDateRavitaillement().format(DATE_FORMATTER)).append("\n");
        recu.append("Lieu: ").append(ravitaillement.getLieu()).append("\n");
        recu.append("N° Ravitaillement: ").append(ravitaillement.getIdRavitaillement()).append("\n");
        
        // Informations du vehicule
        if (ravitaillement.getVehicule() != null) {
            recu.append("Vehicule: ").append(ravitaillement.getVehicule().getMatricule()).append("\n");
            recu.append("Marque/Modele: ").append(ravitaillement.getVehicule().getMarque()).append(" ").append(ravitaillement.getVehicule().getModele()).append("\n");
        }
        
        // Informations du conducteur
        recu.append("Conducteur: ").append(ravitaillement.getConducteur()).append("\n");
        
        // Informations de la carte carburant
        if (ravitaillement.getCarteCarburant() != null) {
            recu.append("Carte carburant: ").append(ravitaillement.getCarteCarburant().getNumeroCarte()).append("\n");
            recu.append("Titulaire: ").append(ravitaillement.getCarteCarburant().getTitulaire()).append("\n");
            recu.append("Plafond: ").append(String.format("%.2f", ravitaillement.getCarteCarburant().getPlafond())).append(" DH\n");
        }
        
        recu.append("\n");
        recu.append("DETAILS DU CARBURANT:\n");
        recu.append("-".repeat(30)).append("\n");
        recu.append("Quantite: ").append(String.format("%.2f", ravitaillement.getQuantite())).append(" litres\n");
        recu.append("Prix unitaire: ").append(String.format("%.2f", ravitaillement.getPrixUnitaire())).append(" DH/litre\n");
        recu.append("Montant total: ").append(String.format("%.2f", ravitaillement.getMontantTotal())).append(" DH\n");
        recu.append("Kilometrage: ").append(ravitaillement.getKilometrageReleve()).append(" km\n\n");
        
        // Historique des ravitaillements
        if (historique != null && !historique.isEmpty()) {
            recu.append("HISTORIQUE DES RAVITAILLEMENTS:\n");
            recu.append("-".repeat(50)).append("\n");
            
            recu.append(String.format("%-5s %-12s %-20s %-8s %-10s %-12s %-10s\n", 
                    "ID", "Date", "Lieu", "Litres", "Prix/L", "Total", "Km"));
            recu.append("-".repeat(90)).append("\n");
            
            double totalLitresHistorique = 0;
            double totalMontantHistorique = 0;
            
            for (Ravitaillement r : historique) {
                recu.append(String.format("%-5d %-12s %-20s %-8.2f %-10.2f %-12.2f %-10d\n",
                        r.getIdRavitaillement(),
                        r.getDateRavitaillement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        r.getLieu().length() > 20 ? r.getLieu().substring(0, 17) + "..." : r.getLieu(),
                        r.getQuantite(),
                        r.getPrixUnitaire(),
                        r.getMontantTotal(),
                        r.getKilometrageReleve()));
                
                totalLitresHistorique += r.getQuantite();
                totalMontantHistorique += r.getMontantTotal();
            }
            
            recu.append("-".repeat(90)).append("\n");
            recu.append(String.format("%-47s %-8.2f %-10s %-12.2f\n", 
                    "TOTAUX HISTORIQUE:", totalLitresHistorique, "", totalMontantHistorique));
            
            // Statistiques de l'historique
            recu.append("\n");
            recu.append("STATISTIQUES DE L'HISTORIQUE:\n");
            recu.append("-".repeat(40)).append("\n");
            recu.append("Nombre de ravitaillements: ").append(historique.size()).append("\n");
            recu.append("Consommation moyenne: ").append(String.format("%.2f", totalLitresHistorique / historique.size())).append(" litres/ravitaillement\n");
            recu.append("Cout moyen par litre: ").append(String.format("%.2f", totalMontantHistorique / totalLitresHistorique)).append(" DH\n");
            
            // Calcul de la consommation entre les ravitaillements
            if (historique.size() > 1) {
                // Trier par date pour calculer les distances
                List<Ravitaillement> historiqueTrie = new ArrayList<>(historique);
                historiqueTrie.sort((r1, r2) -> r1.getDateRavitaillement().compareTo(r2.getDateRavitaillement()));
                
                double totalKm = 0;
                double totalLitresConsommes = 0;
                
                for (int i = 1; i < historiqueTrie.size(); i++) {
                    Ravitaillement precedent = historiqueTrie.get(i - 1);
                    Ravitaillement actuel = historiqueTrie.get(i);
                    
                    double kmParcourus = actuel.getKilometrageReleve() - precedent.getKilometrageReleve();
                    double litresConsommes = precedent.getQuantite();
                    
                    if (kmParcourus > 0) {
                        totalKm += kmParcourus;
                        totalLitresConsommes += litresConsommes;
                    }
                }
                
                if (totalKm > 0) {
                    double consommationMoyenne = (totalLitresConsommes / totalKm) * 100;
                    recu.append("Consommation moyenne: ").append(String.format("%.2f", consommationMoyenne)).append(" litres/100km\n");
                }
            }
        }
        
        recu.append("\n");
        recu.append("=".repeat(80)).append("\n");
        recu.append("           RECU DETAILLE GENERE LE ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        recu.append("=".repeat(80)).append("\n");
        
        return recu.toString();
    }
    
    /**
     * Genere un nom de fichier pour le recu
     */
    public String genererNomFichier(Ravitaillement ravitaillement) {
        String date = ravitaillement.getDateRavitaillement().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        return "Recu_Ravitaillement_" + ravitaillement.getIdRavitaillement() + "_" + date + ".txt";
    }
}
