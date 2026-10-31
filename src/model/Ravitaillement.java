package model;

import java.time.LocalDateTime;

public class Ravitaillement {
    private int idRavitaillement;
    private LocalDateTime dateRavitaillement;
    private String lieu;
    private double quantite;
    private double prixUnitaire;
    private double montantTotal;
    private int kilometrageReleve;
    private Vehicule vehicule;
    private Utilisateur utilisateur;
    private CarteCarburant carteCarburant;
    private String conducteur;

    public Ravitaillement() {}

    public Ravitaillement(int idRavitaillement, LocalDateTime dateRavitaillement, String lieu,
                          double quantite, double prixUnitaire, double montantTotal,
                          int kilometrageReleve, Vehicule vehicule, Utilisateur utilisateur,
                          CarteCarburant carteCarburant, String conducteur) {
        this.idRavitaillement = idRavitaillement;
        this.dateRavitaillement = dateRavitaillement;
        this.lieu = lieu;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantTotal = montantTotal;
        this.kilometrageReleve = kilometrageReleve;
        this.vehicule = vehicule;
        this.utilisateur = utilisateur;
        this.carteCarburant = carteCarburant;
        this.conducteur = conducteur;
    }

    public int getIdRavitaillement() {
        return idRavitaillement;
    }

    public void setIdRavitaillement(int idRavitaillement) {
        this.idRavitaillement = idRavitaillement;
    }

    public LocalDateTime getDateRavitaillement() {
        return dateRavitaillement;
    }

    public void setDateRavitaillement(LocalDateTime dateRavitaillement) {
        this.dateRavitaillement = dateRavitaillement;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
        recalculerMontant();
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        recalculerMontant();
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    private void recalculerMontant() {
        this.montantTotal = this.quantite * this.prixUnitaire;
    }

    public int getKilometrageReleve() {
        return kilometrageReleve;
    }

    public void setKilometrageReleve(int kilometrageReleve) {
        this.kilometrageReleve = kilometrageReleve;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public CarteCarburant getCarteCarburant() {
        return carteCarburant;
    }

    public void setCarteCarburant(CarteCarburant carteCarburant) {
        this.carteCarburant = carteCarburant;
    }

    public String getConducteur() {
        return conducteur;
    }

    public void setConducteur(String conducteur) {
        this.conducteur = conducteur;
    }

    @Override
    public String toString() {
        return "Ravitaillement{" +
                "id=" + idRavitaillement +
                ", vehicule=" + (vehicule != null ? vehicule.getMatricule() : "N/A") +
                ", date=" + dateRavitaillement +
                ", montant=" + String.format("%.2f", montantTotal) + " DH" +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ravitaillement r = (Ravitaillement) obj;
        return idRavitaillement == r.idRavitaillement;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idRavitaillement);
    }
}
