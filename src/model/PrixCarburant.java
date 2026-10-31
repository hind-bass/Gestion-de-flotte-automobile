package model;

import java.time.LocalDate;

public class PrixCarburant {
    private int idPrix;
    private LocalDate datePrix;
    private double prix;
    private Carburant carburant;

    // Getters et Setters
    public int getIdPrix() { return idPrix; }
    public void setIdPrix(int idPrix) { this.idPrix = idPrix; }

    public LocalDate getDatePrix() { return datePrix; }
    public void setDatePrix(LocalDate datePrix) { this.datePrix = datePrix; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public Carburant getCarburant() { return carburant; }
    public void setCarburant(Carburant carburant) { this.carburant = carburant; }
}