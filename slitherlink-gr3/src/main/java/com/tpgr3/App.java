package com.tpgr3;

import java.util.List;
import com.tpgr3.Techniques.Techniques3diag;

public class App {
    public static void main(String[] args) {
        
        /*Initialisation des valeurs de la grille */
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 0}
        };

        
        /*Creation de la grille */
        Grille grille = new Grille(valeurs);
        grille.afficher();



        // Création d'une instance de votre technique
        Techniques3diag technique = new Techniques3diag();
        
        // Test de la technique
        boolean estApplicable = technique.estApplicable(grille);
        
        // Affichage du résultat
        System.out.println("La technique des cases 3 adjacentes est applicable: " + estApplicable);
    }
}