package model;

import java.time.LocalDate;
import java.util.Objects;

public class Vehicule {
    private String matricule;
    private String marque;
    private String modele;
    private int kilometrageInitial;
    private int kilometrageActuel;
    private LocalDate dateMiseService;
    private double consoTheorique;
    private Carburant carburant;

    public Vehicule() {}

    // Getters et Setters (inchangés)
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public int getKilometrageInitial() { return kilometrageInitial; }
    public void setKilometrageInitial(int kilometrageInitial) { this.kilometrageInitial = kilometrageInitial; }
    public int getKilometrageActuel() { return kilometrageActuel; }
    public void setKilometrageActuel(int kilometrageActuel) { this.kilometrageActuel = kilometrageActuel; }
    public LocalDate getDateMiseService() { return dateMiseService; }
    public void setDateMiseService(LocalDate dateMiseService) { this.dateMiseService = dateMiseService; }
    public double getConsoTheorique() { return consoTheorique; }
    public void setConsoTheorique(double consoTheorique) { this.consoTheorique = consoTheorique; }
    public Carburant getCarburant() { return carburant; }
    public void setCarburant(Carburant carburant) { this.carburant = carburant; }

    // --- Méthodes utilitaires ---

    @Override
    public String toString() {
        return matricule + " - " + marque + " " + modele;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicule vehicule = (Vehicule) o;
        return Objects.equals(matricule, vehicule.matricule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricule);
    }
    
    // ✅ SUPPRESSION : Les méthodes inutiles avec "UnsupportedOperationException" ont été retirées.
}
