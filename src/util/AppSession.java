package util;

import model.Utilisateur;

public class AppSession {
    private static Utilisateur utilisateurConnecte;
    
    public static void setUtilisateur(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }
    
    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }
    
    public static boolean estConnecte() {
        return utilisateurConnecte != null;
    }
    
    public static void deconnecter() {
        utilisateurConnecte = null;
    }
    
    public static String getNomUtilisateur() {
        return utilisateurConnecte != null ? utilisateurConnecte.getNom() : "Invité";
    }
    
    public static String getRoleUtilisateur() {
        return utilisateurConnecte != null && utilisateurConnecte.getRole() != null 
            ? utilisateurConnecte.getRole().getNom() 
            : "Aucun";
    }
}