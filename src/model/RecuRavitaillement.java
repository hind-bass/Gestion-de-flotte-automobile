package model;

import java.time.LocalDateTime;
import java.util.List;

public class RecuRavitaillement {
    private Ravitaillement ravitaillement;
    private List<Ravitaillement> historiqueConsommation;
    private double totalConsomme;
    private double montantTotalConsomme;
    private double prixMoyen;
    private double autonomieRestante;
    private String periodeConsommation;
    
    public RecuRavitaillement(Ravitaillement ravitaillement) {
        this.ravitaillement = ravitaillement;
    }
    
    // Getters et Setters
    public Ravitaillement getRavitaillement() {
        return ravitaillement;
    }
    
    public void setRavitaillement(Ravitaillement ravitaillement) {
        this.ravitaillement = ravitaillement;
    }
    
    public List<Ravitaillement> getHistoriqueConsommation() {
        return historiqueConsommation;
    }
    
    public void setHistoriqueConsommation(List<Ravitaillement> historiqueConsommation) {
        this.historiqueConsommation = historiqueConsommation;
        calculerStatistiques();
    }
    
    public double getTotalConsomme() {
        return totalConsomme;
    }
    
    public void setTotalConsomme(double totalConsomme) {
        this.totalConsomme = totalConsomme;
    }
    
    public double getMontantTotalConsomme() {
        return montantTotalConsomme;
    }
    
    public void setMontantTotalConsomme(double montantTotalConsomme) {
        this.montantTotalConsomme = montantTotalConsomme;
    }
    
    public double getPrixMoyen() {
        return prixMoyen;
    }
    
    public void setPrixMoyen(double prixMoyen) {
        this.prixMoyen = prixMoyen;
    }
    
    public double getAutonomieRestante() {
        return autonomieRestante;
    }
    
    public void setAutonomieRestante(double autonomieRestante) {
        this.autonomieRestante = autonomieRestante;
    }
    
    public String getPeriodeConsommation() {
        return periodeConsommation;
    }
    
    public void setPeriodeConsommation(String periodeConsommation) {
        this.periodeConsommation = periodeConsommation;
    }
    
    /**
     * Calcule les statistiques de consommation basées sur l'historique
     */
    private void calculerStatistiques() {
        if (historiqueConsommation == null || historiqueConsommation.isEmpty()) {
            return;
        }
        
        // Calculer le total consommé
        this.totalConsomme = historiqueConsommation.stream()
            .mapToDouble(Ravitaillement::getQuantite)
            .sum();
        
        // Calculer le montant total
        this.montantTotalConsomme = historiqueConsommation.stream()
            .mapToDouble(Ravitaillement::getMontantTotal)
            .sum();
        
        // Calculer le prix moyen
        this.prixMoyen = this.montantTotalConsomme / this.totalConsomme;
        
        // Calculer l'autonomie restante si une carte carburant est disponible
        if (ravitaillement.getCarteCarburant() != null) {
            double plafond = ravitaillement.getCarteCarburant().getPlafond();
            double montantRestant = plafond - this.montantTotalConsomme;
            
            if (montantRestant > 0) {
                this.autonomieRestante = montantRestant / this.prixMoyen;
            } else {
                this.autonomieRestante = 0;
            }
        }
        
        // Déterminer la période de consommation
        if (historiqueConsommation.size() > 1) {
            LocalDateTime premiereDate = historiqueConsommation.stream()
                .map(Ravitaillement::getDateRavitaillement)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
            
            LocalDateTime derniereDate = historiqueConsommation.stream()
                .map(Ravitaillement::getDateRavitaillement)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
            
            long jours = java.time.Duration.between(premiereDate, derniereDate).toDays();
            if (jours == 0) {
                this.periodeConsommation = "Même jour";
            } else if (jours < 30) {
                this.periodeConsommation = jours + " jours";
            } else if (jours < 365) {
                long mois = jours / 30;
                this.periodeConsommation = mois + " mois";
            } else {
                long annees = jours / 365;
                this.periodeConsommation = annees + " an(s)";
            }
        } else {
            this.periodeConsommation = "Premier ravitaillement";
        }
    }
    
    /**
     * Génère un résumé du reçu
     */
    public String genererResume() {
        StringBuilder resume = new StringBuilder();
        
        resume.append("Résumé de consommation:\n");
        resume.append("- Période: ").append(periodeConsommation).append("\n");
        resume.append("- Total consommé: ").append(String.format("%.2f", totalConsomme)).append(" litres\n");
        resume.append("- Montant total: ").append(String.format("%.2f", montantTotalConsomme)).append(" DH\n");
        resume.append("- Prix moyen: ").append(String.format("%.2f", prixMoyen)).append(" DH/litre\n");
        
        if (autonomieRestante > 0) {
            resume.append("- Autonomie restante: ").append(String.format("%.2f", autonomieRestante)).append(" litres\n");
        }
        
        return resume.toString();
    }
}
