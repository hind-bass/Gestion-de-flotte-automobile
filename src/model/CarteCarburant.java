package model;

public class CarteCarburant {
    private int idCarte;
    private String numeroCarte;
    private String titulaire;
    private double plafond;
    private String statut;

    // Getters et Setters
    public int getIdCarte() { return idCarte; }
    public void setIdCarte(int idCarte) { this.idCarte = idCarte; }

    public String getNumeroCarte() { return numeroCarte; }
    public void setNumeroCarte(String numeroCarte) { this.numeroCarte = numeroCarte; }

    public String getTitulaire() { return titulaire; }
    public void setTitulaire(String titulaire) { this.titulaire = titulaire; }

    public double getPlafond() { return plafond; }
    public void setPlafond(double plafond) { this.plafond = plafond; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return numeroCarte + " (" + statut + ")";
    }
}