package model;

public class Carburant {
    private int idCarburant;
    private String nomCarburant;

    public Carburant() {}

    public Carburant(int idCarburant, String nomCarburant) {
        this.idCarburant = idCarburant;
        this.nomCarburant = nomCarburant;
    }

    // Getters et Setters
    public int getIdCarburant() { return idCarburant; }
    public void setIdCarburant(int idCarburant) { this.idCarburant = idCarburant; }
    public String getNomCarburant() { return nomCarburant; }
    public void setNomCarburant(String nomCarburant) { this.nomCarburant = nomCarburant; }

    @Override
    public String toString() {
        return nomCarburant;
    }
    
    // ✅ SUPPRESSION : Les méthodes inutiles avec "UnsupportedOperationException" ont été retirées.
}
