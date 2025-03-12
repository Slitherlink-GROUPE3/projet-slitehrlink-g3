package com.tpgr3;

import com.tpgr3.Grille;
import java.util.concurrent.TimeUnit;
import com.tpgr3.Case;
import com.tpgr3.Slot;
import com.tpgr3.Cellule;
import com.tpgr3.Techniques.Techniques3diag; 

public class App extends MySleep{
    public static void main(String[] args) {
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 0}
        };

        

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